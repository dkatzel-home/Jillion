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

package org.jcvi.common.core.assembly.ace.consed;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.SortedMap;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ContigBuilder;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigBuilder;
import org.jcvi.common.core.assembly.ace.AceContigTestUtil;
import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.ace.AcePlacedReadBuilder;
import org.jcvi.common.core.assembly.ace.DefaultAceContig;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedUtil_Split0x {

    private final String originalId="origId";
    private final String referenceConsensus = "AACGTACGTAAACGTACGTAA";
    
    private static class TestAceBuilder implements AceContigBuilder{
    	private final AceContigBuilder builder;
    	TestAceBuilder(String id, String consensus){
    		builder = DefaultAceContig.createBuilder(id,consensus);
    	}

    	TestAceBuilder addRead(String readId, String gappedBasecalls,int offset, Direction dir, Range validRange, PhdInfo phdInfo){
    	    builder.addRead(readId, new NucleotideSequenceBuilder(gappedBasecalls).build(),offset,dir,validRange,phdInfo,offset+gappedBasecalls.length());
    		return this;
    	}
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AcePlacedRead, AceContig> setContigId(
                String contigId) {
            return builder.setContigId(contigId);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String getContigId() {
            return builder.getContigId();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int numberOfReads() {
            return builder.numberOfReads();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AcePlacedRead, AceContig> addRead(
                AcePlacedRead placedRead) {
            return builder.addRead(placedRead);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AcePlacedRead, AceContig> addAllReads(
                Iterable<AcePlacedRead> reads) {
            return builder.addAllReads(reads);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void removeRead(String readId) {
            builder.removeRead(readId);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getConsensusBuilder() {
            return builder.getConsensusBuilder();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AceContig build() {
            return builder.build();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AceContigBuilder addRead(String readId, NucleotideSequence validBases,
                int offset, Direction dir, Range clearRange, PhdInfo phdInfo,
                int ungappedFullLength) {
            return builder.addRead(readId, validBases, offset, dir, clearRange, phdInfo, ungappedFullLength);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AcePlacedReadBuilder getPlacedReadBuilder(String readId) {
            return builder.getPlacedReadBuilder(readId);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Collection<AcePlacedReadBuilder> getAllPlacedReadBuilders() {
            return builder.getAllPlacedReadBuilders();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AceContigBuilder setComplimented(boolean complimented) {
            builder.setComplimented(complimented);
            return this;
        }
    	
    }
    @Test
    public void contigWithNo0xRegionsShouldNotTrim(){       
        TestAceBuilder contigBuilder =
        	new TestAceBuilder(originalId,referenceConsensus)
        .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                Direction.FORWARD, 
                Range.create(0, 10), 
                createMock(PhdInfo.class))
        .addRead("read2", referenceConsensus.substring(10), 10, 
                Direction.FORWARD, 
                Range.create(0, 11), 
                createMock(PhdInfo.class));
        
        final SortedMap<Range,AceContig> actualcontigs = ConsedUtil.split0xContig(contigBuilder, false);
        assertEquals(1,actualcontigs.size());
        AceContig expected = new TestAceBuilder(originalId,referenceConsensus)
                                .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                                        Direction.FORWARD, 
                                        Range.create(0, 10), 
                                        createMock(PhdInfo.class))
                                .addRead("read2", referenceConsensus.substring(10), 10, 
                                        Direction.FORWARD, 
                                        Range.create(0, 11), 
                                        createMock(PhdInfo.class))
                                        .build();
        Range expectedRange = Range.create(0,20);
        assertEquals(expectedRange, actualcontigs.firstKey());
        AceContigTestUtil.assertContigsEqual(expected, actualcontigs.get(expectedRange));
    }
    
    @Test
    public void one0xRegionShouldSplitContigIn2(){
        final PhdInfo read1Phd = createMock(PhdInfo.class);
        final PhdInfo read2Phd = createMock(PhdInfo.class);

        TestAceBuilder contig = new TestAceBuilder(originalId,referenceConsensus)
		        .addRead("read1", referenceConsensus.substring(0, 11), 0, 
		                Direction.FORWARD, 
		                Range.create(0, 10), 
		                read1Phd)
		        .addRead("read2", referenceConsensus.substring(12), 12, 
		                Direction.FORWARD, 
		                Range.create(0, 9), 
		                read2Phd);
		       
    
        SortedMap<Range,AceContig> splitContigs = ConsedUtil.split0xContig(contig,  false);
        assertEquals("# of split contigs", 2, splitContigs.size());
        
        AceContig expectedFirstContig = new TestAceBuilder(
                String.format("%s_%d_%d",originalId,1,11),referenceConsensus.substring(0, 11))
                            .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                                    Direction.FORWARD, 
                                    Range.create(0, 10), 
                                    read1Phd)
                                    .build();
        AceContig expectedSecondContig = new TestAceBuilder(
                String.format("%s_%d_%d",originalId,13,21),referenceConsensus.substring(12))
                        .addRead("read2", referenceConsensus.substring(12), 0, 
                                Direction.FORWARD, 
                                Range.create(0, 9), 
                                read2Phd)
                                    .build();
        assertContigsEqual(expectedFirstContig, splitContigs.get(Range.create(0,10)));
        assertContigsEqual(expectedSecondContig, splitContigs.get(Range.create(12,20)));
    }
    
    @Test
    public void contigIdAlreadyHasCoordinatesAtTheEnd_ShouldModifyThoseCoordinates(){

        final PhdInfo read1Phd = createMock(PhdInfo.class);
        final PhdInfo read2Phd = createMock(PhdInfo.class);
        TestAceBuilder contig = new TestAceBuilder("id_1_12",referenceConsensus)
        
		        .addRead("read1", referenceConsensus.substring(0, 11), 0, 
		                Direction.FORWARD, 
		                Range.create(0, 10), 
		                read1Phd)
		        .addRead("read2", referenceConsensus.substring(12), 12, 
		                Direction.FORWARD, 
		                Range.create(0, 9), 
		                read2Phd);
        SortedMap<Range,AceContig> splitContigs = ConsedUtil.split0xContig(contig, true);
        
        assertEquals("# of split contigs", 2, splitContigs.size());
        
        AceContig expectedFirstContig = new TestAceBuilder(
                String.format("id_%d_%d",1,11),referenceConsensus.substring(0, 11))
                            .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                                    Direction.FORWARD, 
                                    Range.create(0, 10), 
                                    read1Phd)
                                    .build();
        AceContig expectedSecondContig = new TestAceBuilder(
                String.format("id_%d_%d",13,21),referenceConsensus.substring(12))
                        .addRead("read2", referenceConsensus.substring(12), 0, 
                                Direction.FORWARD, 
                                Range.create(0, 9), 
                                read2Phd)
                                    .build();
        assertContigsEqual(expectedFirstContig, splitContigs.get(Range.create(0,10)));
        assertContigsEqual(expectedSecondContig, splitContigs.get(Range.create(12,20)));
    }
    
    private void assertContigsEqual(AceContig expected, AceContig actual){
        assertEquals("id",expected.getId(),actual.getId());
        assertEquals("consensus", expected.getConsensus().asList(), actual.getConsensus().asList());
        assertEquals("numberOfReads", expected.getNumberOfReads(), actual.getNumberOfReads());
        CloseableIterator<AcePlacedRead> iter = null;
        try{
        	iter = expected.getReadIterator();
        	while(iter.hasNext()){
        		AcePlacedRead expectedRead = iter.next();
				final String id = expectedRead.getId();
				assertTrue("missing read " + id, actual.containsRead(id));
				assertAcePlacedReadsEqual(expectedRead, actual.getRead(id));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }

    /**
     * @param expectedRead
     * @param placedReadById
     */
    private void assertAcePlacedReadsEqual(AcePlacedRead expected,
            AcePlacedRead actual) {
        assertEquals("id",expected.getId(),actual.getId());
        assertEquals("offset",expected.getBegin(),actual.getBegin());
        assertEquals("direction",expected.getDirection(),actual.getDirection());
        
        assertEquals("phdInfo",expected.getPhdInfo(),actual.getPhdInfo());
        assertEquals("basecalls",expected.getNucleotideSequence().asList(),actual.getNucleotideSequence().asList());
        assertEquals("validRange",expected.getValidRange(),actual.getValidRange());
    }
}
