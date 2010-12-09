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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.fasta.fastq.illumina.AllIlluminaUnitTests;
import org.jcvi.fasta.fastq.solexa.AllSolexaUnitTests;
import org.jcvi.fasta.fastq.util.AllFastQUtilUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestSangerFastQQualityCodec.class,
        TestSangerFastQQualityCodecActual.class,
        TestDefaultFastQFileDataStore.class,
        TestLargeFastQFileDataStore.class,
        TestParseSangerEncodedFastQFile.class,
        AllIlluminaUnitTests.class,
        AllSolexaUnitTests.class,
        TestFastQUtil.class,
        AllFastQUtilUnitTests.class,
        TestIndexedFastQFileDataStore.class,
        
        TestH2NucleotideFastQDataStore.class,
        TestFilteredH2NucleotideFastQDataStore.class,
        TestH2QualityFastQDataStore.class,
        TestFilteredH2QualityFastQDataStore.class
        
    }
    )
public class AllFastqUnitTests {

}
