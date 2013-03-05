package org.jcvi.jillion.assembly.ace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

final class ConsedConsensusQualityComputer {

	//The algorithm that consed uses was explained to dkatzel
	//by David Gordon, the author of Consed, via several phone calls and emails 
	//in March 2013
	//
	//Here is the basic algorithm:
	//
	//For each consensus position, look at all underlying reads that match
	//and sum the highest forward quality and the highest reverse quality
	//if there is extra coverage (even if only in one dir) then add an additional 5 qv.
	//max value allowed is 90.
	//
	//To filter which reads are considered, Consed uses a flanking window of 2bp on each side
	//the entire window must match the consensus in order for the read to be considered at 
	//the consensus position.
	//
	//below are some more details from emails from David Gordon:
	//
	//OK, I looked at the code for computing the consensus qualities. 
	//It only uses reads that agree with the consensus in a window about the base in question:
	//
	//    ...CCBCC...    consensus
	//    ...ccbcc...    read
	//
	// so in a column, if any of ccbcc disagrees with CCBCC,
	// this read is not used for the purpose of calculating the consensus quality of B.
	//
	// at the ends of contigs, then the window is one-sided.  For
    // example, at the left end the window looks like this:
	//
    //    BCC...
    //    bcc...
	//
    //  (even if the read extends further to the left).
	//
	// And all of the bases must not be pads.  So if there is a column of
	// pads, the window is larger:
	//
	//      ...C*CCBCC...
	//       ...c*ccbcc...
	//
	// If this window isn't completely contained within the read's aligned
	// region, then this read isn't used.
	//
	// The whole window business is to not allow mis-aligned reads to be used
	// in the calculation.
	//
	// Requiring the window decreases the chance that the read is misaligned
	// (at this location).  When you look at it this way, then these rules
	// make sense.
	//
	//
	// The +5 is used only once--not once for each strand.
	//
	// There is also an issue of library duplicates. 
	// If 2 reads have the same starting location, they are suspected
	// of being library duplicates and thus are not allowed to 
	// confirm each other so no +5 boost is given.  
	// For example, suppose you have 50 reads all top strand,
	// but they all start at the same location, 
	// then there is no +5 of the quality because it is likely 
	// they were all PCR'd from the same piece of DNA.
	//
	// Quality values are not allowed to be greater than 90--I just cut it off there. 
	// (Quality 98 and 99 have special meanings.)
	
	/**
	 * {@value}  = Max value that consed qualities are allowed to have,
	 * any qualities values that are greater have special meanings.
	 */
	private static final int MAX_CONSED_COMPUTED_QUALITY = 90;
	/**
	 * Bonus quality amount added to final consensus quality
	 * if there are multiple reads that agree with the 
	 * consensus in a single direction that don't
	 * start at the same position.
	 */
	private static final int BONUS_VALUE = 5;
	
	private static final int NUMBER_OF_NON_GAPS_IN_WINDOW =2;
	
	private final Contig<? extends AssembledRead> contig;
	private final QualitySequenceDataStore readQualities;
	
	
	ConsedConsensusQualityComputer(Contig<? extends AssembledRead> contig, QualitySequenceDataStore readQualities){
		if(contig ==null){
    		throw new NullPointerException("contig can not be null");
    	}
		if(readQualities ==null){
			throw new NullPointerException("read quality datastore can not be null");
		}
		this.contig = contig;
		this.readQualities = readQualities;
	}
	/**
     * Compute the consensus quality sequence as computed by the same algorithm consed uses.
     * @param contig the contig to compute the consensus qualities for; can not be null.
     * @return a {@link QualitySequence} can not be null.
     * @throws DataStoreException 
     * @throws NullPointerException if contig is null.
     */
    public QualitySequence computeConsensusQualities() throws DataStoreException{
    	
    	
    	
    	NucleotideSequence consensusSequence = contig.getConsensusSequence();
    	int[] consensusGapsArray = toIntArray(consensusSequence.getGapOffsets());
    	
    	int consensusLength = (int)consensusSequence.getLength();
		List<List<QualityPosition>> forwardQualitiesTowardsConsensus = new ArrayList<List<QualityPosition>>((int)consensusSequence.getLength());
		List<List<QualityPosition>> reverseQualitiesTowardsConsensus = new ArrayList<List<QualityPosition>>((int)consensusSequence.getLength());
    	
		for(int i=0; i< consensusLength; i++){
    		forwardQualitiesTowardsConsensus.add(new ArrayList<QualityPosition>());
    		reverseQualitiesTowardsConsensus.add(new ArrayList<QualityPosition>());
    	}
		
		StreamingIterator<? extends AssembledRead> iter= contig.getReadIterator();
    	try{
	    	while(iter.hasNext()){
	    		AssembledRead read = iter.next();
	    		long start =read.getGappedStartOffset();
	    		
	    		int[] differenceArray = toIntArray( read.getNucleotideSequence().getDifferenceMap().keySet());	    		
	    		int[] readGaps = toIntArray(read.getNucleotideSequence().getGapOffsets());
	    		Range validRange = read.getReadInfo().getValidRange();
	    		Direction dir = read.getDirection();
	    		if(dir ==Direction.REVERSE){
	    			validRange = AssemblyUtil.reverseComplementValidRange(validRange, read.getReadInfo().getUngappedFullLength());
	    		}
	    		QualitySequence validQualities = AssemblyUtil.getUngappedComplementedValidRangeQualities(read, readQualities.get(read.getId()));

	    		Iterator<PhredQuality> qualIter = validQualities.iterator();
	    		int i=0;
	    		while(qualIter.hasNext()){
	    			if(notAGap(readGaps, i)){
		    			PhredQuality qual =qualIter.next();
		    			int consensusOffset = (int)(i+start);
		    			if(notAGap(consensusGapsArray, consensusOffset)){
			    			if(readMatchesWindow(consensusGapsArray, consensusLength, read, start, differenceArray, i)){
			    				QualityPosition position = new QualityPosition(qual, start);
			    				if(dir==Direction.FORWARD){
			    					forwardQualitiesTowardsConsensus.get(consensusOffset).add(position);
			    				}else{
			    					reverseQualitiesTowardsConsensus.get(consensusOffset).add(position);
			    				}
			    			}
		    			}
	    			}
	    			i++;
	    		}
	    	}
	    	//we've now looked through all the reads
	    	QualitySequenceBuilder consensusQualitiesBuilder = new QualitySequenceBuilder(consensusLength);
	    	for(int i=0; i< consensusLength; i++){
	    		consensusQualitiesBuilder.append(
	    				computeConsensusQuality( forwardQualitiesTowardsConsensus,reverseQualitiesTowardsConsensus, i));
	    	}
	    	removeConsensusGaps(consensusQualitiesBuilder,consensusGapsArray);
	    	return consensusQualitiesBuilder.build();
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
	private void removeConsensusGaps(
			QualitySequenceBuilder consensusQualitiesBuilder,
			int[] consensusGapsArray) {
		//iterate backwards to preserve offset order
		for(int i=consensusGapsArray.length-1; i>=0; i--){
			consensusQualitiesBuilder.delete(Range.of(consensusGapsArray[i]));
		}
		
	}
	
	private boolean notDifferentThan(int[] differenceArray, int offset){
		return notAGap(differenceArray, offset);
	}
	private boolean notAGap(int[] consensusGapsArray,
			int consensusOffset) {
		return Arrays.binarySearch(consensusGapsArray, consensusOffset)<0;
	}
	private int computeConsensusQuality(
			List<List<QualityPosition>> forwardQualitiesTowardsConsensus,
			List<List<QualityPosition>> reverseQualitiesTowardsConsensus, int i) {
		List<QualityPosition> forwards = forwardQualitiesTowardsConsensus.get(i);
		Collections.sort(forwards);	    		
		
		List<QualityPosition> reverses = reverseQualitiesTowardsConsensus.get(i);
		Collections.sort(reverses);
		
		byte highestForwardQuality = forwards.isEmpty()? 0 : forwards.get(forwards.size()-1).quality;
		byte highestReverseQuality = reverses.isEmpty()? 0 : reverses.get(reverses.size()-1).quality;
  	
		int sum = highestForwardQuality + highestReverseQuality;
		if(hasBonusCoverage(forwards) || hasBonusCoverage(reverses)){
			sum +=BONUS_VALUE;
		}
		return Math.min(sum, MAX_CONSED_COMPUTED_QUALITY);
	}
	public boolean readMatchesWindow(int[] consensusGapsArray,
			int consensusLength, AssembledRead read, long start,
			int[] differenceArray, int i) {
		boolean windowMatches = notDifferentThan(differenceArray, i);
		int windowLeftSize = computeWindowLeft(consensusGapsArray, i+ start);
		
		for(int j= i-windowLeftSize; windowMatches && j>=0 && j<i; j++){
			windowMatches = notDifferentThan(differenceArray, j);
		}
		if(windowMatches){
			//short circuit so we don't do any extra computations
			int windowRightSize = computeWindowRight(consensusGapsArray, i+start, consensusLength);
			for(int j= i+1; windowMatches && j<=i+windowRightSize && j<read.getGappedLength(); j++){
				windowMatches = notDifferentThan(differenceArray, j);
			}
		}
		return windowMatches;
	}
    private int[] toIntArray(Collection<Integer> ints){
    	int[] array = new int[ints.size()];
		Iterator<Integer> iter = ints.iterator();
		for(int i=0;  iter.hasNext(); i++){
			array[i]=iter.next().intValue();
		}
		return array;
    }
    private int computeWindowLeft(int[] consensusGapsArray, long startPosition) {
		int numberOfBasesInWindow=0;
		int position = (int)startPosition-1;
		while(position >=0 && numberOfBasesInWindow < NUMBER_OF_NON_GAPS_IN_WINDOW){
			
			if(notAGap(consensusGapsArray, position)){
				//not a gap
				numberOfBasesInWindow++;
			}
			position--;
		}
		return numberOfBasesInWindow;
	}
    
    private int computeWindowRight(int[] consensusGapsArray, long startPosition, int consensusLength) {
		int numberOfBasesInWindow=0;
		int position = (int)startPosition+1;
		int numberOfNonGapsInWindow=0;
		while(position <consensusLength && numberOfNonGapsInWindow < NUMBER_OF_NON_GAPS_IN_WINDOW){
			
			if(notAGap(consensusGapsArray, position)){
				//not a gap
				numberOfNonGapsInWindow++;				
			}
			numberOfBasesInWindow++;
			position++;
		}
		return numberOfBasesInWindow;
	}
    
    
	private boolean hasBonusCoverage(List<QualityPosition> forwards) {
		if(forwards.isEmpty()){
			return false;
		}
		Iterator<QualityPosition> iter = forwards.iterator();
		long firstOffset = iter.next().startOffset;
		while(iter.hasNext()){
			long nextOffset = iter.next().startOffset;
			if(firstOffset !=nextOffset){
				return true;
			}
		}
		return false;
	}
	private static final class QualityPosition implements Comparable<QualityPosition>{
    	private byte quality;
    	private long startOffset;
    	
		public QualityPosition(PhredQuality quality, long startOffset) {
			this.quality = quality.getQualityScore();
			this.startOffset = startOffset;
		}

		@Override
		public int compareTo(QualityPosition other) {			
			return quality- other.quality;
		}
    	
    }
}