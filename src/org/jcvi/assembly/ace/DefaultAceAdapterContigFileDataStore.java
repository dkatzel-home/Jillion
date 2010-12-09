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
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.assembly.contig.DefaultContigFileParser;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.util.CloseableIterator;

public class DefaultAceAdapterContigFileDataStore extends AbstractAceAdaptedContigFileDataStore implements AceContigDataStore{

    private final Map<String, AceContig> map = new HashMap<String, AceContig>();
    private DataStore<AceContig> dataStore;
    
    /**
     * @param phdDate
     */
    public DefaultAceAdapterContigFileDataStore(Date phdDate) {
        super(phdDate);
    }
    public DefaultAceAdapterContigFileDataStore(Date phdDate, File contigFile) throws FileNotFoundException{
        this(phdDate);
        DefaultContigFileParser.parse(contigFile, this);
    }
    @Override
    protected void visitAceContig(DefaultAceContig aceContig) {
        map.put(aceContig.getId(), aceContig);        
    }

    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        dataStore = new SimpleDataStore<AceContig>(map);
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return dataStore.contains(id);
    }

    @Override
    public AceContig get(String id) throws DataStoreException {
        return dataStore.get(id);
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return dataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return dataStore.size();
    }

    @Override
    public void close() throws IOException {
        dataStore.close();
        
    }

    @Override
    public CloseableIterator<AceContig> iterator() {
        return dataStore.iterator();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return dataStore.isClosed();
    }

}
