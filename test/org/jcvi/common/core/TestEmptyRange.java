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
 * Created on Aug 9, 2007
 *
 * @author dkatzel
 */
package org.jcvi.common.core;


import static org.junit.Assert.*;

import org.jcvi.common.core.Range;
import org.junit.Test;
public class TestEmptyRange{

    private Range emptyRange = new Range.Builder().build();
    private Range nonEmptyRange = Range.of(5,5);


    @Test
    public void testSize(){
        assertEquals(0,emptyRange.getLength());
    }
    @Test
    public void testIsEmpty(){
        assertTrue(emptyRange.isEmpty());
    }
    @Test
    public void testIsSubRangeOf(){
        assertFalse(emptyRange.isSubRangeOf(nonEmptyRange));
    }
    @Test
    public void testEndsBefore(){
        assertTrue(emptyRange.endsBefore(nonEmptyRange));
        assertFalse(new Range.Builder()
        			.shift(10)
        			.build().endsBefore(nonEmptyRange));
    }
    @Test
    public void testIntersects(){
        assertFalse(emptyRange.intersects(nonEmptyRange));
    }


    @Test
    public void testIntersection(){
        assertSame(emptyRange,emptyRange.intersection(nonEmptyRange));
    }
    @Test
    public void testStartsBefore(){
        assertTrue(emptyRange.startsBefore(nonEmptyRange));
    }
    
}
