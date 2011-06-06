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

package org.jcvi.fastX.fasta.pos;

import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.util.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class PositionDataStoreAdapter implements PositionDataStore{

    private final DataStore<EncodedGlyphs<ShortGlyph>> datastore;
    
    
    public PositionDataStoreAdapter(
            DataStore<EncodedGlyphs<ShortGlyph>> datastore) {
        this.datastore = datastore;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public EncodedGlyphs<ShortGlyph> get(String id) throws DataStoreException {
        return datastore.get(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return datastore.isClosed();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        datastore.close();
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<EncodedGlyphs<ShortGlyph>> iterator() {
        return datastore.iterator();
    }

}
