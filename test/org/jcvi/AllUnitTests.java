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
 * Created on Apr 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

import junit.framework.Test;

import org.jcvi.align.AllAlignTests;
import org.jcvi.app.AllAppUnitTests;
import org.jcvi.assembly.AllAssemblyUnitTests;
import org.jcvi.assembly.ace.AllAceUnitTests;
import org.jcvi.auth.AllAuthUnitTests;
import org.jcvi.cli.AllCliUnitTests;
import org.jcvi.command.AllCommandUnitTests;
import org.jcvi.datastore.AllDataStoreUnitTests;
import org.jcvi.fasta.AllFastaUnitTests;
import org.jcvi.glyph.AllGlyphUnitTests;
import org.jcvi.glyph.qualClass.AllQualityClassUnitTests;
import org.jcvi.http.AllHttpUnitTests;
import org.jcvi.io.AllIOUnitTests;
import org.jcvi.sequence.AllSequencingTests;
import org.jcvi.trace.AllTraceUnitTests;
import org.jcvi.util.AllUtilUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        
        TestCommonUtil.class,
        MathUtilSuite.class,
        TestTestUtilSuite.class,
        TestDistance.class,
        TestRange.class,
        TestRangeArrivalComparator.class,
        TestRangeDepartureComparator.class,
        TestEmptyRange.class,
        TestRangeIterator.class,
        
        AllHttpUnitTests.class,
        AllGlyphUnitTests.class,
        AllFastaUnitTests.class,
        AllAceUnitTests.class,
        AllQualityClassUnitTests.class,
        AllSequencingTests.class,
        org.jcvi.log.AllUnitTests.class,
        AllIOUnitTests.class,
        AllTraceUnitTests.class,
        AllAssemblyUnitTests.class,
        AllUtilUnitTests.class,
        AllDataStoreUnitTests.class,
        AllAlignTests.class,
        AllAuthUnitTests.class,
        AllCliUnitTests.class,
        AllAppUnitTests.class,
        AllCommandUnitTests.class
    }
)
public class AllUnitTests {
  //required for ant?
    public static Test suite() {
        return new junit.framework.JUnit4TestAdapter(AllUnitTests.class);
     }
    public static void main(String[] args)  {
        junit.textui.TestRunner.run(AllUnitTests.suite());
    }
}
