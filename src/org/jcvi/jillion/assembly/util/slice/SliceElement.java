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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.slice;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code SliceElement} is 
 * @author dkatzel
 *
 *
 */
public interface SliceElement {
    /**
     * Get the {@link Nucleotide} of this SliceElement.
     * @return
     */
    Nucleotide getBase();
    /**
     * Get the {@link PhredQuality} of this SliceElement.
     * @return
     */
    PhredQuality getQuality();
    /**
     * Get the {@link Direction} of this SliceElement.
     * @return
     */
    Direction getSequenceDirection();
}