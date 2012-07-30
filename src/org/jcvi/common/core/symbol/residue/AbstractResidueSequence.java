package org.jcvi.common.core.symbol.residue;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Range;
/**
 * {@code AbstractResidueSequence} is an abstract implementation of 
 * {@link ResidueSequence} that implements some of the common methods
 * that are required by {@link ResidueSequence}.  These implementations
 * should be considered default implementations and may be overridden
 * by subclasses if a more efficient method can be used instead.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} in this sequence.
 */
public abstract class AbstractResidueSequence<R extends Residue> implements ResidueSequence<R>{

	@Override
    public long getUngappedLength(){
        return getLength() - getNumberOfGaps();
    }
    @Override
    public int getNumberOfGapsUntil(int gappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapOffsets()){
            if(gapIndex.intValue() <=gappedValidRangeIndex){
                numberOfGaps++;
            }
        }
        return numberOfGaps;
    }
    private int computeNumberOfInclusiveGapsInUngappedValidRangeUntil(int ungappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapOffsets()){
            //need to account for extra length
        	//due to gaps being added to ungapped index
            if(gapIndex.intValue() <=ungappedValidRangeIndex + numberOfGaps){
                numberOfGaps++;
            }
        }
        return numberOfGaps;
    }

   
    /**
     * Default implementation gets the {@link Residue}s
     * to put in the returned list by successive calls
     * to {@link #get(int)} for all the desired offsets
     * in the given range.  This method should be overridden
     * if many {@link #get(int)} calls is not an efficient way to pull out the data.
     * </p>
     * {@inheritDoc}
     */
    @Override
    public List<R> asList(Range range) {
        if(range==null){
            return asList();
        }
        List<R> result = new ArrayList<R>();
        for(long i = range.getBegin(); i<= range.getEnd(); i++){
        	result.add(get((int)i));
        }
        return result;
    }

   
    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedOffsetFor(int gappedIndex) {
        return gappedIndex - getNumberOfGapsUntil(gappedIndex);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getGappedOffsetFor(int ungappedIndex) {
        return ungappedIndex +computeNumberOfInclusiveGapsInUngappedValidRangeUntil(ungappedIndex);
    }
}
