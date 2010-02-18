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
 * Created on Jan 20, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment.score;

import org.jcvi.assembly.cas.alignment.CasAlignmentType;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCasAlignmentType {

    @Test
    public void valueOf(){
        assertSame(CasAlignmentType.LOCAL, CasAlignmentType.valueOf((byte)0));
        assertSame(CasAlignmentType.SEMI_LOCAL, CasAlignmentType.valueOf((byte)1));
        assertSame(CasAlignmentType.REVERSE_SEMI_LOCAL, CasAlignmentType.valueOf((byte)2));
        assertSame(CasAlignmentType.GLOBAL, CasAlignmentType.valueOf((byte)3));
    }
}
