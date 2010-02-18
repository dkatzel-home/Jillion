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
 * Created on Feb 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
public class TestDefaultQualityClassContigMap {
    CoverageMap<CoverageRegion<PlacedRead>> coverageMap;
    EncodedGlyphs<NucleotideGlyph> consensus;
    DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap;
    QualityClassComputer<PlacedRead, NucleotideGlyph> qualityClassComputer;
   DefaultQualityClassContigMap sut;
   @Before
   public void setup(){
       coverageMap = createMock(CoverageMap.class);
       consensus= createMock(EncodedGlyphs.class);
       qualityFastaMap= createMock(DataStore.class);
       qualityClassComputer= createMock(QualityClassComputer.class);
   }
    @Test
    public void emtpyConsensusShouldReturnEmptyMap(){
        expect(consensus.getLength()).andReturn(0L).atLeastOnce();
        replay(coverageMap,consensus,qualityFastaMap,qualityClassComputer);
        DefaultQualityClassContigMap sut = new DefaultQualityClassContigMap(coverageMap,consensus,
                qualityFastaMap,qualityClassComputer);
        assertTrue(sut.getQualityClassRegions().isEmpty());
        verify(coverageMap,consensus,qualityFastaMap,qualityClassComputer);
        
    }
}
