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
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jcvi.jillion.assembly.util.CoverageRegion;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultScaffold {

    Scaffold scaffold;
    Set<PlacedContig> placedContigs;

    long scaffoldLength;

    @Before
    public void setUp() {
        placedContigs = new HashSet<PlacedContig>(5);
        placedContigs.add(new DefaultPlacedContig("contig1", Range.of(28,100), Direction.FORWARD));
        placedContigs.add(new DefaultPlacedContig("contig2", Range.of(250,375), Direction.REVERSE));
        placedContigs.add(new DefaultPlacedContig("contig3", Range.of(320,383), Direction.REVERSE));
        placedContigs.add(new DefaultPlacedContig("contig4", Range.of(390,500), Direction.REVERSE));
        placedContigs.add(new DefaultPlacedContig("contig5", Range.of(628,707), Direction.FORWARD));
        scaffoldLength = 707-28+1;


        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        for ( PlacedContig contig : placedContigs ) {
            builder.add(contig.getContigId(),contig.asRange(), contig.getDirection());
        }
        scaffold = builder.build();
    }

    @Test
    public void shiftingContigsShouldAdjustCoordinatesToStartAtZero(){
    	 ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
    	 builder.shiftContigsToOrigin(true);
    	 for ( PlacedContig contig : placedContigs ) {
    		 builder.add(contig);    
    	 }
    	 Scaffold shiftedScaffold = builder.build();
    	 long shiftOffset = getFirstCoveredRange(scaffold).getBegin();
    	 for(PlacedContig shiftedContig : shiftedScaffold.getPlacedContigs()){
    		 assertShiftedCoorrectly(scaffold.getPlacedContig(shiftedContig.getContigId()), shiftedContig,shiftOffset);
    	 }
    }
    private Range getFirstCoveredRange(Scaffold scaffold) {
		for(CoverageRegion<?> region :scaffold.getContigCoverageMap()){
			if(region.getCoverageDepth()>0){
				return region.asRange();
			}
		}
		throw new IllegalStateException("scaffold is empty");
	}

	private void assertShiftedCoorrectly(PlacedContig unshifted,
			PlacedContig shifted, long shiftOffset) {
		assertEquals(unshifted.getContigId(), shifted.getContigId());
		assertEquals(unshifted.getDirection(), shifted.getDirection());
		assertEquals(new Range.Builder(unshifted.asRange())
						.shift(-shiftOffset)
						.build(), shifted.asRange());
	}

	@Test
    public void testGetPlacedContig() {
        PlacedContig contig = placedContigs.iterator().next();
        assertEquals(scaffold.getPlacedContig(contig.getContigId()),contig);
    }

    @Test
    public void testGetPlacedContigs() {
        assertEquals(scaffold.getPlacedContigs(),placedContigs);
    }

    @Test
    public void testGetScaffoldLength() {
        assertEquals(scaffold.getLength(),scaffoldLength);
    }

    @Test
    public void testGetNumberOfContigs() {
        assertEquals(scaffold.getNumberOfContigs(),placedContigs.size());
    }



    @Test(expected= NoSuchElementException.class)
    public void testUnkonwnContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        Scaffold scaffold = builder.build();
        
        scaffold.convertContigRangeToScaffoldRange("nonexistantContig",Range.of(10,48));
    }

    @Test(expected= IllegalArgumentException.class)
    public void testInvalidContigRangeCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.of(0,100), Direction.FORWARD);
        Scaffold scaffold = builder.build();

        scaffold.convertContigRangeToScaffoldRange("contig1",Range.of(50,150));
    }

    @Test(expected= NullPointerException.class)
    public void testInvalidContigDirectionCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.of(0,100),null);
        }

    
    @Test
    public void testSingleForwardContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.of(37,164), Direction.FORWARD);
        Scaffold scaffold = builder.build();

        Range contigRange = Range.of(10,48);
        Range expectedRange = Range.of(47,85);
        Range convertedRange = scaffold.convertContigRangeToScaffoldRange("contig1",contigRange);

        Assert.assertEquals(expectedRange,convertedRange);
    }

    @Test
    public void testSingleReverseContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.of(37,164), Direction.REVERSE);
        Scaffold scaffold = builder.build();

        Range contigRange = Range.of(10,48);
        Range expectedRange = Range.of(116,154);
        Range convertedRange = scaffold.convertContigRangeToScaffoldRange("contig1",contigRange);

        Assert.assertEquals(expectedRange,convertedRange);
    }

    @Test
    public void testMultipleForwardContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.of(37,164), Direction.FORWARD);
        builder.add("contig2", Range.of(293,568), Direction.FORWARD);
        Scaffold scaffold = builder.build();

        Range contigRange = Range.of(31,56);
        Range expectedRange = Range.of(324,349);
        Range convertedRange = scaffold.convertContigRangeToScaffoldRange("contig2",contigRange);

        Assert.assertEquals(expectedRange,convertedRange);
    }

    @Test
    public void testMultipleReverseContigCoordinateConversionTest() {
        ScaffoldBuilder builder = DefaultScaffold.createBuilder("testScaffold");
        builder.add("contig1", Range.of(37,164), Direction.FORWARD);
        builder.add("contig2", Range.of(293,568), Direction.REVERSE);
        Scaffold scaffold = builder.build();

        Range contigRange = Range.of(20,35);
        Range expectedRange = Range.of(533,548);
        Range convertedRange = scaffold.convertContigRangeToScaffoldRange("contig2",contigRange);

        Assert.assertEquals(expectedRange,convertedRange);
    }

}
