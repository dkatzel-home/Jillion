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
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.coverage;


import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.Rangeable;
/**
 * A {@link CoverageRegion} is a contiguious 
 * portion of a {@link CoverageMap} which 
 * has exactly the same elements (and therefore
 * the same depth of coverage). If any elements
 * stop providing coverage or if new elements start
 * providing coverage, then there will be multiple
 * {@link CoverageRegion}s.
 * @author dkatzel
 *
 * @param <T> the type of {@link Rangeable} elements that make up
 * this coverage region.
 */
public interface CoverageRegion<T extends Rangeable> extends Rangeable, Iterable<T> {
    /**
     * Get the coverage depth of this coverage region.
     * which is the number of elements that make up 
     * this region.
     * @return an integer {@code >= 0}.
     */
    int getCoverageDepth();    
    
    StreamingIterator<T> getElementIterator();
}
