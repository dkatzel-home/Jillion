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

package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.util.iter.TestAbstractBlockingClosableIteratorExceptions;
import org.jcvi.jillion.core.util.iter.TestChainedIterator;
import org.jcvi.jillion.core.util.iter.TestEmptyIterator;
import org.jcvi.jillion.core.util.iter.TestPeekableIterator;
import org.jcvi.jillion.core.util.iter.TestPeekableStreamingIterator;
import org.jcvi.jillion.core.util.iter.TestStreamingIterator;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
       
    	TestObjectsUtil.class,
        TestRunLength.class,
        MathUtilSuite.class,
        TestLRUCache.class,
        TestWeakReferenceLRUCache.class,
        TestEmptyIterator.class,
        TestFileIterator.class,
        TestDepthFirstFileIterator.class,
        TestBreadthFirstFileIterator.class,
        TestStringUtilities.class,
        TestMultipleWrapper.class,
        TestChainedIterator.class,
        TestStreamingAdapter.class,
        TestStreamingIterator.class,
        TestMapValueComparator.class,

        TestPeekableIterator.class,
        TestPeekableStreamingIterator.class,
        
        TestAbstractBlockingClosableIteratorExceptions.class,
        TestDateUtilElapsedTime.class,
        TestGrowableByteArray.class,
        TestGrowableShortArray.class
    }
    )
public class AllUtilUnitTests {

}