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
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.phd;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

final class ArtificialPhd implements Phd{
    /**
     * The Position of the first peak in a Newbler created
     * fake 454 phd record.
     */
    private static final int NEWBLER_454_START_POSITION = 15;
    /**
     * The number of positions between every basecall
     * in a Newbler created fake 454 phd record.
     */
    private static final int NEWBLER_454_PEAK_SPACING = 19;
    
    private final NucleotideSequence basecalls;
    private final QualitySequence qualities;
   private final Map<String,String> comments;
   private final List<PhdWholeReadItem> wholeReadItems;
   
   private final List<PhdReadTag> readTags;
   
   private PositionSequence fakePositions=null;
   private final int numberOfPositionsForEachPeak;
   private final int numberOfBases;
   private final int positionOfFirstPeak;
   private final String id;
   /**
    * Create an {@link Phd} record that matches
    * the way Newbler creates phd records for 454 reads.
    * This is needed so tools like consed will correctly
    * space the fake chromatograms for 454 reads since it uses
    * 454 developed tools which rely on this spacing.
    * 
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param comments the comments for this Phd.
    * @param wholeReadItems the {@link PhdWholeReadItem}s for this Phd.
    * @return a new {@link Phd} which has position data that matches
    * what would have been created by Newbler.
    */
   public static ArtificialPhd createNewbler454Phd(
		   String id,
		   NucleotideSequence basecalls,
           QualitySequence qualities,
           Map<String,String> comments, List<PhdWholeReadItem> wholeReadItems){
       return new ArtificialPhd(id,basecalls, qualities, comments, wholeReadItems, 
    		   Collections.<PhdReadTag>emptyList(),
    		   NEWBLER_454_START_POSITION,NEWBLER_454_PEAK_SPACING);
   }
   /**
    * Create an {@link Phd} record that matches
    * the way Newbler creates phd records for 454 reads.
    * This is needed so tools like consed will correctly
    * space the fake chromatograms for 454 reads since it uses
    * 454 developed tools which rely on this spacing.
    * 
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param comments the comments for this Phd.
    * @return a new {@link Phd} which has position data that matches
    * what would have been created by Newbler.
    */
   public static ArtificialPhd createNewbler454Phd(
		   String id,
		   NucleotideSequence basecalls,
           QualitySequence qualities,
           Map<String,String> comments){
	   return ArtificialPhd.createNewbler454Phd(id,basecalls, qualities, 
               comments,Collections.<PhdWholeReadItem>emptyList());
   }
   /**
    * Create an {@link Phd} record that matches
    * the way Newbler creates phd records for 454 reads.
    * This is needed so tools like consed will correctly
    * space the fake chromatograms for 454 reads since it uses
    * 454 developed tools which rely on this spacing.
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @return a new {@link Phd} which has position data that matches
    * what would have been created by Newbler.
    */
   public static ArtificialPhd createNewbler454Phd(String id,
		   NucleotideSequence basecalls,
           QualitySequence qualities){
       return ArtificialPhd.createNewbler454Phd(id,basecalls, qualities, 
               Collections.<String,String>emptyMap(),Collections.<PhdWholeReadItem>emptyList());
   }
   /**
    * {@code buildArtificalPhd} creates a {@link Phd}
    * using the given basecalls and qualities
    * but creates artificial peak data spacing each
    * peak {@code numberOfPositionsForEachPeak} apart.
    * This Phd will have no comments and no tags.
    * 
    *
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param numberOfPositionsForEachPeak number of positions each
    * peak should be separated as.
    * 
    */
   public ArtificialPhd(String id,
		   NucleotideSequence basecalls,
           QualitySequence qualities,
           int numberOfPositionsForEachPeak){
       this(id,basecalls, qualities, 
    		   Collections.<String,String>emptyMap(),
    		   Collections.<PhdWholeReadItem>emptyList(),
    		   Collections.<PhdReadTag>emptyList(),
    		   numberOfPositionsForEachPeak);
   }
   /**
    * {@code buildArtificalPhd} creates a {@link Phd}
    * using the given basecalls and qualities, comments and tags
    * but creates artificial peak data spacing each
    * peak {@code numberOfPositionsForEachPeak} apart.
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param comments the comments for this Phd.
    * @param readTags the {@link PhdReadTag}s for this Phd.
    * @param wholeReadItems the {@link PhdWholeReadItem}s for this Phd.
    * @param numberOfPositionsForEachPeak number of positions each
    * peak should be separated as.
    */
    public ArtificialPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            Map<String,String> comments, 
            List<PhdWholeReadItem> wholeReadItems,
            List<PhdReadTag> readTags,
            int numberOfPositionsForEachPeak){
       this(id,basecalls, qualities,comments, wholeReadItems,readTags, numberOfPositionsForEachPeak,numberOfPositionsForEachPeak);
        
        
    }
    /**
     * {@code buildArtificalPhd} creates a {@link Phd}
     * using the given basecalls and qualities, comments and tags
     * but creates artificial peak data spacing each
     * peak {@code numberOfPositionsForEachPeak} apart.
     * @param id the id for this Phd.
     * @param basecalls the basecalls for this Phd.
     * @param qualities the qualities for this Phd.
     * @param comments the comments for this Phd.
     * @param wholeReadItems the {@link PhdWholeReadItem}s for this Phd.
     *  @param readTags the {@link PhdReadTag}s for this Phd.
     * 
     * @param numberOfPositionsForEachPeak number of positions each
     * peak should be separated as.
     */
     public ArtificialPhd(String id, NucleotideSequence basecalls,
             QualitySequence qualities,
            Map<String,String> comments, List<PhdWholeReadItem> wholeReadItems,
            List<PhdReadTag> readTags,
            int positionOfFirstPeak,int numberOfPositionsForEachPeak){
         this.id = id;
    	 this.basecalls = basecalls;
         this.qualities = qualities;
         this.wholeReadItems = wholeReadItems;
         this.comments = comments;
         this.numberOfBases = (int)basecalls.getLength();
         this.numberOfPositionsForEachPeak = numberOfPositionsForEachPeak;
         this.positionOfFirstPeak = positionOfFirstPeak;
         this.readTags = readTags;
         
     }
    @Override
    public Map<String,String> getComments() {
        return comments;
    }

    @Override
	public List<PhdReadTag> getReadTags() {
		return readTags;
	}
	@Override
	public List<PhdWholeReadItem> getWholeReadItems() {
		return wholeReadItems;
	}
	@Override
    public String getId(){
    	return id;
    }
   

    @Override
    public synchronized PositionSequence getPeakSequence() {
        if(fakePositions ==null){
        	PositionSequenceBuilder builder = new PositionSequenceBuilder(numberOfBases);
            
            for(int i=0; i< numberOfBases; i++){
               builder.append(i * numberOfPositionsForEachPeak +positionOfFirstPeak );
            }
            this.fakePositions = builder.build();
        }
        return fakePositions;
    }

    @Override
    public NucleotideSequence getNucleotideSequence() {
        return basecalls;
    }

    @Override
    public QualitySequence getQualitySequence() {
        return qualities;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((basecalls == null) ? 0 : basecalls.hashCode());
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		PositionSequence positions = getPeakSequence();
		result = prime * result + positions.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((qualities == null) ? 0 : qualities.hashCode());
		result = prime * result + ((wholeReadItems == null) ? 0 : wholeReadItems.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Phd)) {
			return false;
		}
		Phd other = (Phd) obj;
		if (!id.equals(other.getId())) {
			return false;
		}
		if (!basecalls.equals(other.getNucleotideSequence())) {
			return false;
		}
		if (!comments.equals(other.getComments())) {
			return false;
		}
		if (!getPeakSequence().equals(other.getPeakSequence())) {
			return false;
		}
		
		if (!qualities.equals(other.getQualitySequence())) {
			return false;
		}
		if (!wholeReadItems.equals(other.getWholeReadItems())) {
			return false;
		}
		if (!readTags.equals(other.getReadTags())) {
			return false;
		}
		return true;
	}
    
}
