/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AbstractContig;
import org.jcvi.common.core.assembly.util.slice.QualityValueStrategy;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
/**
 * {@code DefaultAceContig} is the default implementation of {@link AceContig}.
 * @author dkatzel
 *
 *
 */
public final class  DefaultAceContigBuilder implements AceContigBuilder{

	
   
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param consensus the initial contig consensus for this contig (may be changed later)
     * @return a new {@link AceContigBuilder} instance; never null.
     * @throws NullPointerException if contigId or consensus are null.
     */
    public static DefaultAceContigBuilder createBuilder(String contigId, String consensus){
        return new DefaultAceContigBuilder(contigId, consensus);
    }
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param consensus the initial contig consensus for this contig (may be changed later)
     * @return a new {@link AceContigBuilder} instance; never null.
     * @throws NullPointerException if contigId or consensus are null.
     */
    public static DefaultAceContigBuilder createBuilder(String contigId, NucleotideSequence consensus){
        return new DefaultAceContigBuilder(contigId, consensus);
    }
    
    
    	/**
    	 * {@link ConsensusCaller} used to update the
    	 * consensus during {@link #build()}.  If set to {@code null},
    	 * then no recalling is to be done (null by default).
    	 */
    	private ConsensusCaller consensusCaller =null;
    	/**
    	 * {@link QualitySequenceDataStore} used during
    	 * consensus recalling.  Set to null if 
    	 * no recalling is to be done. (null by default).
    	 */
    	private QualitySequenceDataStore qualityDataStore =null;
    	
    	private QualityValueStrategy qualityValueStrategy=null;
    	
        private NucleotideSequence fullConsensus;
        private final NucleotideSequenceBuilder mutableConsensus;
        private String contigId;
        private final Map<String, AceAssembledReadBuilder>aceReadBuilderMap = new HashMap<String, AceAssembledReadBuilder>();
        private int contigLeft= -1;
        private int contigRight = -1;
        private volatile boolean built=false;
        private boolean complemented=false;
        public DefaultAceContigBuilder(String contigId, String fullConsensus){
           this(contigId,                   
        		   new NucleotideSequenceBuilder(fullConsensus).build()
            );
        }
       
        public DefaultAceContigBuilder(String contigId, NucleotideSequence fullConsensus){
            if(contigId ==null){
                throw new NullPointerException("contig id can not be null");
            }
            if(fullConsensus ==null){
                throw new NullPointerException("consensus can not be null");
            }
        	this.fullConsensus = fullConsensus;
        	 this.contigId = contigId;
        	 this.mutableConsensus = new NucleotideSequenceBuilder(fullConsensus);
        }
        /**
         * Set this contig as being complemented.
         * @param complemented
         * @return this
         */
        @Override
        public DefaultAceContigBuilder setComplemented(boolean complemented){
            this.complemented = complemented;
            return this;
        }
        /**
         * Recall the consensus using the given
         * {@link ConsensusCaller} and {@link QualitySequenceDataStore}
         * which contains the quality data for all of the reads in this ace contig.
         * The consensus will get recalled inside the {@link #build()}
         * method before the {@link AceContig} instance is created.
         * @param consensusCaller the {@link ConsensusCaller}  instance to use
         * to recall the consensus of this contig; can not be null.
         * @param qualityDataStore the {@link QualitySequenceDataStore}
         * which contains all the quality data for all of the reads
         * in this contig; can not be null.
         * @return this.
         * @throws NullPointerException if any parameter is null.
         */
        public DefaultAceContigBuilder recallConsensus(ConsensusCaller consensusCaller, 
        		QualitySequenceDataStore qualityDataStore,
        		QualityValueStrategy qualityValueStrategy){
        	if(consensusCaller ==null){
        		throw new NullPointerException("consensus caller can not be null");
        	}
        	if(qualityDataStore ==null){
        		throw new NullPointerException("quality datastore can not be null");
        	}
        	this.consensusCaller=consensusCaller;
        	this.qualityDataStore = qualityDataStore;
        	return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAceContigBuilder setContigId(String contigId){
            if(contigId==null){
                throw new NullPointerException("contig id can not be null");
            }
            this.contigId = contigId;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String getContigId() {
            return contigId;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public int numberOfReads(){
            return aceReadBuilderMap.size();
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAceContigBuilder addRead(AceAssembledRead acePlacedRead) {
         return addRead(acePlacedRead.getId(),
        		 acePlacedRead.getNucleotideSequence(),
        		 (int)acePlacedRead.getGappedStartOffset(),
        		 acePlacedRead.getDirection(),
        		 acePlacedRead.getReadInfo().getValidRange(),
        		 acePlacedRead.getPhdInfo(),
        		 acePlacedRead.getReadInfo().getUngappedFullLength());
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAceContigBuilder addAllReads(Iterable<AceAssembledRead> reads){
            for(AceAssembledRead read : reads){
                addRead(read);
            }
            return this;
        }
    	/**
        * {@inheritDoc}
        */
    	@Override
        public Collection<AceAssembledReadBuilder> getAllAssembledReadBuilders(){
    	    return aceReadBuilderMap.values();
    	}
        /**
        * {@inheritDoc}
        */
        @Override
        public AceAssembledReadBuilder getAssembledReadBuilder(String readId){
            return aceReadBuilderMap.get(readId);
        }
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAceContigBuilder removeRead(String readId) {
            if(readId==null){
                throw new NullPointerException("read id can not be null");
            }
            aceReadBuilderMap.remove(readId);
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAceContigBuilder addRead(String readId, NucleotideSequence validBases, int offset,
                Direction dir, Range clearRange,PhdInfo phdInfo,int ungappedFullLength) {
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembly/consensus caller is used.
            //force contig left and right to be within the called consensus
            //BCISD-211
            int correctedOffset = Math.max(0,offset);
            adjustContigLeftAndRight(validBases, correctedOffset);
            AceAssembledReadBuilder aceReadBuilder = createNewAceReadBuilder(readId, validBases, correctedOffset, dir, 
                        clearRange,phdInfo,ungappedFullLength);
                
                
                aceReadBuilderMap.put(readId,aceReadBuilder);
            
            return this;
        }
        private AceAssembledReadBuilder createNewAceReadBuilder(
                String readId, NucleotideSequence validBases, int offset,
                Direction dir, Range clearRange, PhdInfo phdInfo,int ungappedFullLength) {
            return DefaultAceAssembledRead.createBuilder(
                    fullConsensus,readId,
                    validBases,
                    offset,dir,clearRange,phdInfo,ungappedFullLength);
        }
        private void adjustContigLeftAndRight(NucleotideSequence validBases, int offset) {
            adjustContigLeft(offset);
            adjustContigRight(validBases, offset);
        }
        private void adjustContigRight(NucleotideSequence validBases, int offset) {
            final int endOfNewRead = offset+ (int)validBases.getLength()-1;
            if(endOfNewRead <= fullConsensus.getLength() && (contigRight ==-1 || endOfNewRead > contigRight)){
                contigRight = endOfNewRead ;
            }
        }
        private void adjustContigLeft(int offset) {
            
            if(contigLeft ==-1 || offset <contigLeft){
                contigLeft = offset;
            }
        }
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getConsensusBuilder() {
            return mutableConsensus;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public AceContig build(){
             if(built){
                 throw new IllegalStateException("this contig has already been built");
             }
             
            if(numberOfReads()==0){
                //force empty contig if no reads...
            	 built=true;
                return new DefaultAceContigImpl(contigId, new NucleotideSequenceBuilder().build(),Collections.<AceAssembledRead>emptySet(),complemented);
            }
            if(consensusCaller !=null){
            	recallConsensus();
            }
            SortedSet<AceAssembledRead> placedReads = new TreeSet<AceAssembledRead>(ConsedReadComparator.INSTANCE);
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembly/consensus caller is used.
            //force contig left and right to be within the called consensus
            //BCISD-211            
            contigLeft = Math.max(contigLeft, 0);
            contigRight = Math.min(contigRight,(int)mutableConsensus.getLength()-1);
            //here only include the gapped valid range consensus bases
            //throw away the rest            
            NucleotideSequence validConsensus = mutableConsensus
            		.copy()
            		.trim(Range.of(contigLeft, contigRight))
            		.build();
            for(AceAssembledReadBuilder aceReadBuilder : aceReadBuilderMap.values()){
                int newOffset = (int)aceReadBuilder.getBegin() - contigLeft;
                aceReadBuilder.reference(validConsensus,newOffset);
                placedReads.add(aceReadBuilder.build());                
            } 
            built=true;
            aceReadBuilderMap.clear();
            fullConsensus = null;
            
            return new DefaultAceContigImpl(contigId, validConsensus,placedReads,complemented);
        }
    
    private void recallConsensus() {
    	/*
    	CompactedSlice.Builder builders[] = new CompactedSlice.Builder[consensusLength];
        
    	while(readIter.hasNext()){
			PR read = readIter.next();
			int start = (int)read.getGappedStartOffset();
			int i=0;
			String id =read.getId();
			Direction dir = read.getDirection();
			QualitySequence fullQualities =null;
			if(qualityDataStore!=null){
				fullQualities = qualityDataStore.get(id);
    			
    			if(fullQualities ==null){
    				throw new NullPointerException("could not get qualities for "+id);
    			}
			}
			
			for(Nucleotide base : read.getNucleotideSequence()){
				
				final PhredQuality quality;
				if(fullQualities==null){
					quality = DEFAULT_QUALITY;
				}else{
					quality= qualityValueStrategy.getQualityFor(read, fullQualities, i);
				}
				if(builders[start+i] ==null){
					builders[start+i] = new CompactedSlice.Builder();
				}
				builders[start+i].addSliceElement(id, base, quality, dir);
				i++;
			}
		}
		*/
			
	}

	/**
     * Comparator singleton that sorts reads like consed does when outputing ace files.
     * @author dkatzel
     *
     */
    private static enum ConsedReadComparator implements Comparator<AceAssembledRead>{
		INSTANCE;
		
		@Override
		public int compare(AceAssembledRead o1, AceAssembledRead o2) {
			int comp= Range.Comparators.ARRIVAL.compare(o1.asRange(),o2.asRange());
			if(comp!=0){
				return comp;
			}
			//ranges the same order by id
			return o1.getId().compareTo(o2.getId());
		}

	}
    
    private static final class  DefaultAceContigImpl extends AbstractContig<AceAssembledRead> implements AceContig{

    	
        private final boolean complemented;

        private DefaultAceContigImpl(String id, NucleotideSequence consensus,
                Set<AceAssembledRead> reads,boolean complemented) {
            super(id, consensus, reads);
            this.complemented = complemented;
        }
       
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isComplemented() {
            return complemented;
        }



        @Override
    	public int hashCode() {
    		final int prime = 31;
    		int result = super.hashCode();
    		result = prime * result + (complemented ? 1231 : 1237);
    		return result;
    	}
    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) {
    			return true;
    		}
    		if (!super.equals(obj)) {
    			return false;
    		}
    		if (!(obj instanceof DefaultAceContigBuilder)) {
    			return false;
    		}
    		DefaultAceContigBuilder other = (DefaultAceContigBuilder) obj;
    		if (complemented != other.complemented) {
    			return false;
    		}
    		return true;
    	}
    }

}