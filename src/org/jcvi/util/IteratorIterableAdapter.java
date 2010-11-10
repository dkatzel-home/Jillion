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

package org.jcvi.util;

import java.util.Iterator;

/**
 * {@code IteratorIterableAdapter} is an adapter
 * for {@link Iterator}s to adapt them into {@link Iterable}s
 * so that they can be used inside
 * Java 5 enhanced for loops.
 * @author dkatzel
 *
 *
 */
public class IteratorIterableAdapter<T> implements Iterable<T> {
    /**
     * Builder method that handles the generic type
     * information for you so client code is less cluttered.
     * @param <T> the Type being iterated over.
     * @param iterator the iterator instance to adapt.
     * @return a new IteratorIterableAdapter which adapts the given
     * iterator into an iterable.
     */
    public static <T> IteratorIterableAdapter<T> createIterableAdapterFor(Iterator<T> iterator){
        return new IteratorIterableAdapter<T>(iterator);
    }
    private final Iterator<T> iterator;
    
    /**
     * @param iterator
     */
    private IteratorIterableAdapter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

}
