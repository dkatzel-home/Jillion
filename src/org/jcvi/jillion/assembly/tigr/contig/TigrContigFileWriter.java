/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.tigr.contig;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.JillionUtil;
/**
 * {@code TigrContigFileWriter} will write out {@link Contig}
 * objects in ctg format.
 * @author dkatzel
 *
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
		value = {"VA_FORMAT_STRING_USES_NEWLINE"},
		justification = "\n character is required for .contig format "
						+ "we don't want to accidentally put in a \r\n on diffent OS")
public class TigrContigFileWriter implements Closeable{
    private static final int INITIAL_RECORD_BUFFER_SIZE = 2048;
	private static final CtgFormatReadSorter READ_SORTER = CtgFormatReadSorter.INSTANCE;
    private final OutputStream out;
    
    public TigrContigFileWriter(File out) throws IOException{
    	IOUtil.mkdirs(out.getParentFile());
    	this.out = new BufferedOutputStream(new FileOutputStream(out));
    }
    public TigrContigFileWriter(OutputStream out) {
        this.out = out;
    }
    public <PR extends AssembledRead, C extends Contig<PR>> void write(C contig) throws IOException,
            UnsupportedEncodingException {
        writeContigHeader(contig);
        writeBases(contig.getConsensusSequence());
        Set<PR> readsInContig = new TreeSet<PR>(READ_SORTER);
        StreamingIterator<PR> iter = null;
        try{
        	iter = contig.getReadIterator();
        	while(iter.hasNext()){
        		PR placedRead = iter.next();
        		readsInContig.add(placedRead);
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        for(AssembledRead placedRead : readsInContig){
            writePlacedReadHeader(placedRead, contig.getConsensusSequence());
            writeBases(placedRead.getNucleotideSequence());
        }
    }
    
   
    private void writeContigHeader(Contig<? extends AssembledRead> contig) throws IOException {
        String header = String.format("##%s %d %d bases\n",
                contig.getId(), contig.getNumberOfReads(), contig.getConsensusSequence().getLength());
        
        writeToOutputStream(header);
    }

    private void writeBases(Sequence<Nucleotide> consensus) throws UnsupportedEncodingException, IOException {
       
        String result = consensus.toString().replaceAll("(.{60})", "$1\n");
        if(!result.endsWith("\n")){
            result += "\n";
        }
        writeToOutputStream(result);
        
    }
    private void writeToOutputStream(final String result) throws IOException,
            UnsupportedEncodingException {
        out.write(result.getBytes("UTF-8"));
    }
    
    private void writePlacedReadHeader(AssembledRead placedRead,NucleotideSequence consensus) throws IOException {
        StringBuilder header = new StringBuilder(INITIAL_RECORD_BUFFER_SIZE);
        header.append(String.format("#%s(%d) [", placedRead.getId(), placedRead.getGappedStartOffset()));
        int validLeft = (int)placedRead.getReadInfo().getValidRange().getBegin();
        int validRight = (int)placedRead.getReadInfo().getValidRange().getEnd();
        if(placedRead.getDirection() == Direction.REVERSE){
            header.append("RC");
            int temp = validLeft;
            validLeft = validRight;
            validRight = temp;
        }

        header.append(String.format("] %d bases {%d %d} <%d %d>\n",
                placedRead.getNucleotideSequence().getLength(), validLeft+1, validRight+1, 
                placedRead.getGappedStartOffset()+1-consensus.getNumberOfGapsUntil((int) placedRead.getGappedStartOffset()), 
                placedRead.getGappedEndOffset()+1-consensus.getNumberOfGapsUntil((int)placedRead.getGappedEndOffset())));
        writeToOutputStream(header.toString());
        
    }
    /**
     * {@code CtgFormatReadSorter} will sort the {@link AssembledRead}s
     * by start coordinate.  If multiple reads have the same start coordinate
     * in the contig, then those reads will be sorted by length (smallest first).  If there are still
     * multiple reads that have the same start AND the same length, then those reads
     * are sorted by their ids.
     * @author dkatzel
     *
     *
     */
    private static enum CtgFormatReadSorter implements Comparator<AssembledRead>, Serializable{
        /**
         * Singleton instance.
         */
        INSTANCE;

        /**
         * Sorts PlacedRead by offset then by read length, then by id.
         */
        @Override
        public int compare(AssembledRead o1, AssembledRead o2) {
        	
            int startComparison = JillionUtil.compare(o1.getGappedStartOffset(), o2.getGappedStartOffset());
            if(startComparison !=0){
                return startComparison;
            }
            int lengthComparison= JillionUtil.compare(o1.getGappedLength(), o2.getGappedLength());
            if(lengthComparison !=0){
                return lengthComparison;
            }
            int idLengthComparison =  JillionUtil.compare(o1.getId().length(), o2.getId().length());
            if(idLengthComparison !=0){
                return idLengthComparison;
            }
            return o1.getId().compareTo(o2.getId());
        }
        
    }
    @Override
    public void close() throws IOException {
        out.close();
        
    }

}
