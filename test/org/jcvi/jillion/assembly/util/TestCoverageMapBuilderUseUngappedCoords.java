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
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.util.ContigCoverageMapBuilder;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageRegion;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Test;
public class TestCoverageMapBuilderUseUngappedCoords {

	@Test
	public void noGapsShouldSameReturnSameAsGappedCoverageMap(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		
		CoverageMap<AssembledRead> gappedCoverageMap = new ContigCoverageMapBuilder<AssembledRead>(contig).build();
		CoverageMap<AssembledRead> ungappedCoverageMap = new ContigCoverageMapBuilder<AssembledRead>(contig)
																.useUngappedCoordinates()
																.build();
		
		assertEquals(gappedCoverageMap, ungappedCoverageMap);
	}
	
	@Test
	public void oneGapInConsensus(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTAC-T")
											.addRead("read1", 0, "ACGTACGT")
											.addRead("read2", 4, "AC-T")
											.build();

		CoverageMap<AssembledRead> gappedCoverageMap = new ContigCoverageMapBuilder<AssembledRead>(contig)
															.build();
		CoverageMap<AssembledRead> ungappedCoverageMap = new ContigCoverageMapBuilder<AssembledRead>(contig)
															.useUngappedCoordinates()
															.build();
		
		assertEquals("ungapped length should be less than gapped length",
				getLastCoveredOffsetIn(gappedCoverageMap), 
				getLastCoveredOffsetIn(ungappedCoverageMap)+1);
		
		assertEquals("number of regions should be the same", gappedCoverageMap.getNumberOfRegions(), ungappedCoverageMap.getNumberOfRegions());
		assertEquals(2, ungappedCoverageMap.getNumberOfRegions());
		assertEquals(gappedCoverageMap.getRegion(0), ungappedCoverageMap.getRegion(0));
		CoverageRegion<AssembledRead> coverageRegion = ungappedCoverageMap.getRegion(1);
		assertEquals(Range.of(4,6), coverageRegion.asRange());
		assertEquals(2, coverageRegion.getCoverageDepth());
		List<String> actualReads = new ArrayList<String>();
		for(AssembledRead read : coverageRegion){
			actualReads.add(read.getId());
		}
		
		assertTrue(actualReads.contains("read1"));
		assertTrue(actualReads.contains("read2"));
	
	}
	
	private static long getLastCoveredOffsetIn(CoverageMap<?> coverageMap){
        if(coverageMap.isEmpty()){
            return -1L;
        }
        return coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd();
}
}
