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
package org.jcvi.jillion.assembly.consed.nav;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;

/**
 * {@code ConsedNavigationWriter} write Consed
 * Custom Navigation Files to allow consed
 * users to quickly jump to feature locations
 * while editing.
 * @author dkatzel
 *
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
		value = {"VA_FORMAT_STRING_USES_NEWLINE"},
		justification = "\n character is required for .ace format (?) "
						+ "we don't want to accidentally put in a \r\n on diffent OS"
						+ "and break consed")
public final class ConsedNavigationWriter implements Closeable{

    private final OutputStream out;
    
    private static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final int NAV_ELEMENT_BUFFER_SIZE = 1024;
    
    public static ConsedNavigationWriter create(String title, OutputStream out) throws IOException{
        if(title ==null){
            throw new NullPointerException("title can not be null");
        }
        return new ConsedNavigationWriter(title, out);
    }
    /**
     * Create a partial consed navigation file without a title.
     * This should only be used if the navigation file creation is being split up 
     * into multiple pieces to be combined downstream.
     * @param out the outputStream to write to; can not be null.
     * @return a new ConsedNavigationWriter
     * @throws NullPointerException if out is {@code null}.
     */
    public static ConsedNavigationWriter createPartial(OutputStream out){
        return new ConsedNavigationWriter(out);
    }
    /**
     * Creates a new ConsedNavigationWriter which will
     * write out navigation data to the given {@link OutputStream}.
     * The navigation file will not contain a title.
     * @param out the OutputStream to write to.
     * @throws NullPointerException if out is {@code null}.
     */
    public ConsedNavigationWriter(OutputStream out){
        if(out ==null){
            throw new NullPointerException("output stream can not be null");
        }        
        this.out= out;
        
    }
    /**
     * Creates a new ConsedNavigationWriter which will
     * write out navigation data to the given {@link OutputStream}.
     * @param title the title of this navigation file;
     * if title is null, then no title will be written.
     * @param out the OutputStream to write to.
     * @throws IOException if there is a problem writing
     * to the outputStream.
     * @throws NullPointerException if out is {@code null}.
     */
    public ConsedNavigationWriter(String title, OutputStream out) throws IOException{
        if(out ==null){
            throw new NullPointerException("output stream can not be null");
        }
        if(title!=null){
            out.write((String.format("TITLE: %s",title)+"\n").getBytes(UTF_8));
        }
        
        this.out= out;
        
    }
    
    public ConsedNavigationWriter(String title, File outFile) throws IOException{
    	this(title, new BufferedOutputStream(new FileOutputStream(outFile)));
    }
    
    @Override
    public void close() throws IOException {
        out.close();
    }
    
    public void writeNavigationElement(ReadNavigationElement element) throws IOException{
    	 Range range = element.getUngappedPositionRange();
    	 String comment = element.getComment();
    	 
        StringBuilder builder = new StringBuilder(NAV_ELEMENT_BUFFER_SIZE)
        		.append("BEGIN_REGION\n")
		        .append(String.format("TYPE: %s\n",element.getType()))
		        .append(String.format("READ: %s\n",element.getTargetId()))
		       
		        .append(String.format("UNPADDED_READ_POS: %d %d\n",
		        		range.getBegin(CoordinateSystem.RESIDUE_BASED), range.getEnd(CoordinateSystem.RESIDUE_BASED)))
		       
		        //consed requires a comment line even if it is empty
		        .append(String.format("COMMENT: %s\n",comment==null? "": comment))
        		.append("END_REGION\n");
        out.write(builder.toString().getBytes(UTF_8));
    }
    
    public void writeNavigationElement(ConsensusNavigationElement element) throws IOException{
    	Range range = element.getUngappedPositionRange();
    	 String comment = element.getComment();
        StringBuilder builder = new StringBuilder(NAV_ELEMENT_BUFFER_SIZE)
        							.append("BEGIN_REGION\n")
							        .append(String.format("TYPE: %s\n",element.getType()))
							        .append(String.format("CONTIG: %s\n",element.getTargetId()))
							        
							        .append(String.format("UNPADDED_CONS_POS: %d %d\n",range.getBegin(CoordinateSystem.RESIDUE_BASED), range.getEnd(CoordinateSystem.RESIDUE_BASED)))
							       
							        //consed requires a comment line even if it is empty
							        .append(String.format("COMMENT: %s\n",comment==null? "": comment))
							       
							        .append("END_REGION\n");
        
        out.write(builder.toString().getBytes(UTF_8));
    }
}
