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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil.ClipPointsType;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.MapUtil;
/**
 * {@code AbstractAceFileVisitor} is the main {@link AceFileVisitor}
 * implementation that will interpret the visit method calls
 * to build valid reads and contigs.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractAceFileVisitor implements AceFileVisitor{
    private String currentContigId;
    private String currentReadId;
    private int currentReadGappedFullLength;
    private int currentReadUngappedFullLength;
    private Map<String, AlignedReadInfo> currentAssembledFromMap;
    private boolean readingConsensus=true;
    private NucleotideSequenceBuilder currentBasecalls = new NucleotideSequenceBuilder();
    private PhdInfo currentPhdInfo;
    private Range currentClearRange;
    private int currentOffset;
    private NucleotideSequence currentValidBases;
    private boolean skipCurrentRead=false;
    private String currentFullLengthBases;
    private int numberOfBasesInCurrentContig;
    private int numberOfReadsInCurrentContig;
    private boolean currentContigIsComplimented=false;
    private boolean initialized;

    public synchronized boolean isInitialized() {
        return initialized;
    }
    protected final String getCurrentFullLengthBasecalls(){
        return currentFullLengthBases;
    }
    @Override
    public synchronized void visitFile() {
        throwExceptionIfInitialized();
    }

    private synchronized void throwExceptionIfInitialized() {
        if(isInitialized()){
            throw new IllegalStateException("already initialized");
        }
    }
    /**
     * Store AF data in map.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public synchronized void visitAssembledFromLine(String readId,
            Direction dir, int gappedStartOffset) {
        throwExceptionIfInitialized();
        fireVisitNewContigIfWeHaventAlready();
        final AlignedReadInfo assembledFromObj = new AlignedReadInfo(gappedStartOffset, dir);
        currentAssembledFromMap.put(readId, assembledFromObj);
    }
    protected synchronized void setAlignedInfoMap(Map<String, AlignedReadInfo> currentAlignedInfoMap){
    	this.currentAssembledFromMap = currentAlignedInfoMap;
    }
    
    
	protected synchronized final Map<String, AlignedReadInfo> getAlignedInfoMap() {
		//defensive copy
		int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(currentAssembledFromMap.size());
		Map<String, AlignedReadInfo> copy = new HashMap<String, AlignedReadInfo>(capacity);
		copy.putAll(currentAssembledFromMap);
		return copy;
	}
	private synchronized void fireVisitNewContigIfWeHaventAlready() {
		if(readingConsensus){
            readingConsensus=false;
           visitNewContig(currentContigId, 
                   currentBasecalls.build(),
                   numberOfBasesInCurrentContig, numberOfReadsInCurrentContig, currentContigIsComplimented);
        }
	}
    /**
     * Begin visiting a new contig in the ace file.  Any visit methods between
     * this call and {@link #visitEndOfContig()} pertain to this contig.
     * @param contigId the ID of the contig being visited.
     * @param consensus the basecalls of the consensus as a NucleotideSequence.
     * @param numberOfBases the total number of bases expected in this contig if you 
     * add the bases from all the reads up.
     * @param numberOfReads the total number of expected reads in this contig.
     * @param isComplemented is this contig complemented
     * @see #visitEndOfContig()
     */
    protected abstract void visitNewContig(String contigId, NucleotideSequence consensus, int numberOfBases, int numberOfReads, boolean isComplemented);

    @Override
    public synchronized void visitConsensusQualities() {
        throwExceptionIfInitialized();

    }

    @Override
    public synchronized void visitBaseSegment(Range gappedConsensusRange, String readId) {
        throwExceptionIfInitialized();

    }
    /**
     * By default this method will always return true.  Please override
     * if your implementation requires only visiting contigs
     * under certain conditions.
     * {@inheritDoc}
     */
    @Override
	public boolean shouldVisitContig(String contigId, int numberOfBases,
			int numberOfReads, int numberOfBaseSegments,
			boolean reverseComplimented) {
		return true;
	}
	/**
     * Reset all temp data that contains contig specific information.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public final synchronized void visitBeginContig(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) {
        throwExceptionIfInitialized();
        currentContigId = contigId;
        currentAssembledFromMap = new HashMap<String, AlignedReadInfo>();
        readingConsensus = true;
        currentBasecalls = new NucleotideSequenceBuilder();
        currentContigIsComplimented = reverseComplimented;
        numberOfBasesInCurrentContig = numberOfBases;
        numberOfReadsInCurrentContig = numberOfReads;
    }

    @Override
    public synchronized void visitHeader(int numberOfContigs, int totalNumberOfReads) {
        throwExceptionIfInitialized();
    }
    /**
     * Uses given quality coordinates to compute
     * the valid range of the current read.
     * If the quality coordinates are not valid,
     * then this read is skipped.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public synchronized void visitQualityLine(int qualLeft,
            int qualRight, int alignLeft, int alignRight) {
        throwExceptionIfInitialized();  
        if(currentReadId ==null){
        	throw new IllegalStateException("current read id is null");
        }
        AlignedReadInfo assembledFrom =currentAssembledFromMap.get(currentReadId);
        if(assembledFrom ==null){
            throw new IllegalStateException("unknown read no AF record for "+ currentReadId);
        }
        Direction readDirection = assembledFrom.getDirection();
        
        ClipPointsType clipPointsType = ConsedUtil.ClipPointsType.getType(qualLeft, qualRight, alignLeft, alignRight);
		if(clipPointsType != ClipPointsType.VALID){
			skipCurrentRead = true;
			switch(clipPointsType){
				case NEGATIVE_VALID_RANGE:					
					 visitIgnoredRead(currentReadId, readDirection, String.format("has a negative valid range %d%n",
			                    (qualRight-qualLeft)));
					 break;
				case ALL_LOW_QUALITY :
			            visitIgnoredRead(currentReadId, readDirection, "entire read is low quality");
			            break;
				case NO_HIGH_QUALITY_ALIGNMENT_INTERSECTION:
		        	visitIgnoredRead(currentReadId,readDirection, "read does not have a high quality aligned range");
		        	break;
	        	default: throw new IllegalStateException("unknown clipPointType "+ clipPointsType);
			}
			return;
		}
		 //dkatzel 4/2011 - There have been cases when qual coords and align coords
        //do not match; usually qual is a sub set of align
        //but occasionally, qual goes beyond the align coords.
        //I guess this happens in a referenced based alignment for
        //reads at the edges when the reads have good quality 
        //beyond the reference.
        //It might also be possible that the read has been 
        //edited and that could have changed the coordinates.
        //Therefore intersect the qual and align coords
        //to find the region we are interested in
        Range qualityRange = Range.create(CoordinateSystem.RESIDUE_BASED, qualLeft,qualRight);
        Range alignmentRange = Range.create(CoordinateSystem.RESIDUE_BASED, alignLeft,alignRight);
        Range gappedValidRange =qualityRange.intersection(alignmentRange);
     
        currentOffset = computeReadOffset(assembledFrom, gappedValidRange.getBegin(CoordinateSystem.RESIDUE_BASED));            
       
        currentFullLengthBases = currentBasecalls.toString();
        NucleotideSequence gappedFullLengthSequence = currentBasecalls.build();
      //this will set currentValidBasecalls to only be the valid range
        currentValidBases =  currentBasecalls.copy().trim(gappedValidRange)
        						.build();
        final int numberOfFullLengthGaps = gappedFullLengthSequence.getNumberOfGaps();
        currentReadUngappedFullLength = currentReadGappedFullLength - numberOfFullLengthGaps;
        //dkatzel 2011-11-18
        //It is possible that there are gaps outside of the valid
        //range (maybe from editing the ace in consed?)
        //we need to account for that
        //the one problem is that this could cause minor
        //differences if we then re-write the ace since
        //we will lose the gaps outside of the valid range
        //but that won't affect real assembly data
        //it will only show up if both versions (before and after)
        //of the file were diff'ed.
        int ungappedClearLeft = gappedFullLengthSequence.getUngappedOffsetFor((int)gappedValidRange.getBegin());
        int ungappedClearRight = gappedFullLengthSequence.getUngappedOffsetFor((int)gappedValidRange.getEnd());
        Range ungappedValidRange = Range.create(CoordinateSystem.RESIDUE_BASED, ungappedClearLeft+1, ungappedClearRight+1 );
        if(readDirection == Direction.REVERSE){
            ungappedValidRange = AssemblyUtil.reverseComplementValidRange(ungappedValidRange, currentReadUngappedFullLength);            
        }
        currentClearRange = ungappedValidRange;
        
    }
    
    
	
    /**
     * During parsing, it might be determined that the current read
     * getting parsed is invalid for various reasons;
     * if that is the case, then this method is called and all further
     * processing of this read is not performed.  Parsing will continue
     * with the next read.
     * <p/>
     * By default the read name and reason are printed
     * to STDERR; please override this method if you want
     * to do something else.
     * @param readId the id of the read getting ignored.
     * @param direction the {@link Direction} of this read.
     * @param reason the reason this read is to be ignored.
     */
    protected void visitIgnoredRead(String readId, Direction direction, String reason){
    	System.err.printf("ignoring read %s because %s%n", readId,reason);
    }
    
    private int computeReadOffset(AlignedReadInfo assembledFrom, long startPosition) {
        return assembledFrom.getStartOffset() + (int)startPosition -2;
    }

    @Override
    public synchronized void visitLine(String line) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitReadHeader(String readId, int gappedLength) {
        throwExceptionIfInitialized();
        currentReadId = readId;
        currentReadGappedFullLength = gappedLength;
        currentBasecalls = new NucleotideSequenceBuilder();
    }

    @Override
    public synchronized void visitTraceDescriptionLine(String traceName, String phdName,
            Date date) {
        throwExceptionIfInitialized();
        if(!skipCurrentRead){
            currentPhdInfo =new DefaultPhdInfo(traceName, phdName, date);
            AlignedReadInfo assembledFrom = currentAssembledFromMap.get(currentReadId);
            visitAceRead(currentReadId, currentValidBases ,currentOffset, assembledFrom.getDirection(), 
                    currentClearRange ,currentPhdInfo,currentReadUngappedFullLength);
        }
        skipCurrentRead=false;
    }
    /**
     * Visit an AceRead inside the current contig.  All the math and coordinate conversions
     * have already been computed from the Ace file already.
     * <p/>
     * This method will be called after the line that triggers the {@link #visitTraceDescriptionLine(String, String, Date)}
     * but before the next {@link #visitLine(String)} is called.
     * @param readId the id of the read.
     * @param validBasecalls the trimmed gapped basecalls of this read.
     * @param offset the 0-based start offset of this read into the contig.
     * @param dir the direction of this read.
     * @param validRange the validRange coordinates of this read's basecalls.
     * @param phdInfo the {@link PhdInfo} for this read (not null).
     * @param ungappedFullLength the full Length (including invalid range)
     * of the basecalls.
     */
    protected abstract void visitAceRead(String readId, NucleotideSequence validBasecalls, 
            int offset, Direction dir, Range validRange, PhdInfo phdInfo,
            int ungappedFullLength);
    
    
    @Override
    public synchronized void visitBasesLine(String bases) {
        throwExceptionIfInitialized();
        currentBasecalls.append(bases.trim());
    }

    @Override
    public synchronized void visitEndOfFile() {
        throwExceptionIfInitialized();  
        clearTempData();
        initialized = true;
        
    }
    
    private void clearTempData(){
        currentContigId = null;
        currentAssembledFromMap = null;
        currentBasecalls = null;
    }

    @Override
    public synchronized void visitBeginConsensusTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitWholeAssemblyTag(String type, String creator,
            Date creationDate, String data) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitConsensusTagComment(String comment) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitConsensusTagData(String data) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitEndConsensusTag() {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitReadTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        throwExceptionIfInitialized();
        
    }
    /**
     * {@inheritDoc}
     */
     @Override
     public boolean visitEndOfContig() {
         //if the contig has 0 reads
         //then we don't have AF records
         //so we need to check if we need to call
         //visit new contig here as well.
         fireVisitNewContigIfWeHaventAlready();
         return true;
     
     }
}
