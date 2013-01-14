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
 * Created on Jun 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.internal.datastore;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code DataStoreIterator}
 * is a simple {@link StreamingIterator}
 * implementations meant for use by DataStores
 * to iterate over its contents using the same
 * order as {@link DataStore#idIterator()}.
 * This class uses the id iterator to get the next
 * id and then calls {@link DataStore#get(String)}
 * with that id; it is not an efficient algorithm
 * but is acceptable if a DataStore implementation
 * does not have a better way to create the iterator.
 * @author dkatzel
 *
 * @param <T>
 */
public final class DataStoreIterator<T> implements StreamingIterator<T>{
    private StreamingIterator<String> ids; 
    private final DataStore<T> dataStore;
    public DataStoreIterator(DataStore<T> dataStore){
        this.dataStore =  dataStore;
        try {
            ids = dataStore.idIterator();
        } catch (DataStoreException e) {
        	IOUtil.closeAndIgnoreErrors(ids);
            throw new IllegalStateException("could not iterate over ids", e);
        }
    }
    @Override
    public boolean hasNext() {
        return ids.hasNext();
    }

    @Override
    public T next() {
        try {
            return dataStore.get(ids.next());
        } catch (DataStoreException e) {
        	IOUtil.closeAndIgnoreErrors(ids);
            throw new IllegalStateException("could not get next element", e);
        }
    }

    @Override
    public void remove() {
       throw new UnsupportedOperationException("can not remove");
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        ids.close();
        
    }
}
