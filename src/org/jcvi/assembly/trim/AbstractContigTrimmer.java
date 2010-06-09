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

package org.jcvi.assembly.trim;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

/**
 * {@code AbstractContigTrimmer} is an abstract implementation
 * of {@link ContigTrimmer} that applies a series
 * of {@link PlacedReadTrimmer}s to reads in a given contig
 * to build a new trimmed version of that contig.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractContigTrimmer<P extends PlacedRead, C extends Contig<P>> implements ContigTrimmer<P,C> {

    private final List<PlacedReadTrimmer<P,C>> trimmers = new ArrayList<PlacedReadTrimmer<P,C>>();
    
    
    /**
     * Construct a new {@link AbstractContigTrimmer} using the given read trimmers.
     * These read trimmers will be executed in the order they are given so chain trimmers
     * appropriately.
     * @param trimmers a non-null list of read trimmers.
     * @throws NullPointerException if trimmers is null.
     * @throws IllegalArgumentException if trimmers is empty.
     */
    public AbstractContigTrimmer(List<PlacedReadTrimmer<P, C>> trimmers) {
        if(trimmers.isEmpty()){
            throw new IllegalArgumentException("trimmer list can not be null");
        }
        this.trimmers.addAll(trimmers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C trimContig(C contig,CoverageMap<CoverageRegion<P>> coverageMap) throws TrimmerException{
        
        initializeTrimmers(contig,coverageMap);
        beginTrimmingContig(contig);
        for(P placedRead : contig.getPlacedReads()){
            Range oldValidRange = placedRead.getValidRange();
            
            Range newTrimRange = computeNewTrimRangeFor(placedRead);
            //skip reads that are completely trimmed
            if(newTrimRange.isEmpty()){
                continue;
            }
            long newOffset = placedRead.convertValidRangeIndexToReferenceIndex((int)newTrimRange.getStart());
            
            final NucleotideEncodedGlyphs originalGappedValidBases = placedRead.getEncodedGlyphs();
            final List<NucleotideGlyph> trimedBasecalls = originalGappedValidBases.decode(newTrimRange);
            String trimmedBases = NucleotideGlyph.convertToString(trimedBasecalls);
            long ungappedLength = new DefaultNucleotideEncodedGlyphs(trimedBasecalls).getUngappedLength();
            
            
            final Range ungappedNewValidRange;
            if(placedRead.getSequenceDirection()==SequenceDirection.FORWARD){
                int numberOfGapsTrimmedOff= originalGappedValidBases.computeNumberOfInclusiveGapsInGappedValidRangeUntil((int)newTrimRange.getStart());
                ungappedNewValidRange = Range.buildRangeOfLength(oldValidRange.getStart()+ newTrimRange.getStart()-numberOfGapsTrimmedOff, ungappedLength).convertRange(CoordinateSystem.RESIDUE_BASED);
                
            }else{
                int numberOfGapsTrimmedOffLeft = originalGappedValidBases.computeNumberOfInclusiveGapsInGappedValidRangeUntil((int)newTrimRange.getStart());
                long numberOfBasesTrimmedOffLeft = newTrimRange.getStart()-numberOfGapsTrimmedOffLeft;
                
                long numberOfBasesTrimmedOffRight = originalGappedValidBases.getUngappedLength() -ungappedLength-numberOfBasesTrimmedOffLeft;
                ungappedNewValidRange = Range.buildRange(oldValidRange.getStart()+numberOfBasesTrimmedOffRight, oldValidRange.getEnd()- numberOfBasesTrimmedOffLeft).convertRange(CoordinateSystem.RESIDUE_BASED);    
            }
            
            trimRead(placedRead, newOffset, trimmedBases,ungappedNewValidRange);
        }
        clearTrimmers();
        return buildNewContig();
    }

    /**
     * 
     */
    private void clearTrimmers() {
        for(PlacedReadTrimmer<P,C> trimmer : trimmers){
            trimmer.clear();
        }
        
    }

    /**
     * @param contig
     * @param coverageMap
     */
    private void initializeTrimmers(C contig,
            CoverageMap<CoverageRegion<P>> coverageMap) {
        for(PlacedReadTrimmer<P,C> trimmer : trimmers){
            trimmer.initializeContig(contig, coverageMap);
        }
        
    }

    /**
     * Trimming has completed, construct a new Contig instance
     * of the contig with the new trim data.
     * @return a new instance of contig with the new trimming data;
     * or null if the entire contig was trimmed off.
     */
    protected abstract  C buildNewContig();

    /**
     * Trim the given PlacedRead with the given current trimmed
     * offset, basecalls and validRange.  If validRange is empty, then 
     * current the read is completely trimmed off.
     * @param placedRead the original untrimmed placedread
     * @param trimmedOffset the current trimmed start offset of this 
     * read in the contig.
     * @param trimmedBasecalls the current trimmed gapped basecalls.
     * @param newValidRange the current trimmed valid range (ungapped)
     * of the read.
     * 
     */
    protected abstract void trimRead(P placedRead, long trimmedOffset, 
            String trimmedBasecalls,Range newValidRange);

    /**
     * This trimmer will begin trimming the given contig, any calls
     * to {@link #trimRead(PlacedRead, long, String, Range)} will be
     * for reads from this contig until the method {@link #buildNewContig()}.
     * @param contig the original contig that will be trimmed.
     */
    protected abstract void beginTrimmingContig(C contig);

    /**
     * @param placedRead
     * @param coverageMap
     * @return
     */
    private Range computeNewTrimRangeFor(P placedRead) {
        Range currentValidRange = Range.buildRangeOfLength(0,placedRead.getLength());
        for(PlacedReadTrimmer<P,C> trimmer : trimmers){
            currentValidRange =trimmer.trimRead(placedRead, currentValidRange);
        }
        return currentValidRange;
    }

}
