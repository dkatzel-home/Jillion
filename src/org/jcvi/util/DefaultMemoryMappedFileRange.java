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
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.Range;

public class DefaultMemoryMappedFileRange implements MemoryMappedFileRange{

    Map<String, Range> ranges;
    
    public DefaultMemoryMappedFileRange(){
        ranges = new HashMap<String, Range>();
    }
    
    @Override
    public boolean contains(String id) {
        return ranges.containsKey(id);
    }

    @Override
    public Range getRangeFor(String id) {
        return ranges.get(id);
    }

    @Override
    public void put(String id, Range range) {
        ranges.put(id,range);
    }

    @Override
    public void close() {
        ranges.clear();
    }

    @Override
    public Iterator<String> getIds() {
        return ranges.keySet().iterator();
    }

    @Override
    public int size() {
        return ranges.size();
    }

    @Override
    public void remove(String id) {
        ranges.remove(id);
        
    }
    
    

}
