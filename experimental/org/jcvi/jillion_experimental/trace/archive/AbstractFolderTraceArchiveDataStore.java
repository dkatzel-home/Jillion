/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion_experimental.trace.archive;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;

public abstract class AbstractFolderTraceArchiveDataStore implements TraceArchiveDataStore<TraceArchiveTrace> {

    private final String rootDirPath;
    private final TraceArchiveInfo traceArchiveInfo;
    
    public String getRootDirPath() {
        return rootDirPath;
    }

    public TraceArchiveInfo getTraceArchiveInfo() {
        return traceArchiveInfo;
    }

    @Override
    public boolean isClosed() {
        return traceArchiveInfo.isClosed();
    }

    @Override
    public void close() throws IOException {
        traceArchiveInfo.close();
    }

    /**
     * @param rootDirPath
     * @param traceArchiveInfo
     */
    public AbstractFolderTraceArchiveDataStore(String rootDirPath,
            TraceArchiveInfo traceArchiveInfo) {
        if(rootDirPath ==null || traceArchiveInfo ==null){
            throw new NullPointerException("arguments must not be null");
        }
        this.rootDirPath = rootDirPath;
        this.traceArchiveInfo = traceArchiveInfo;
    }

    @Override
    public boolean contains(String id) throws DataStoreException{       
            return traceArchiveInfo.contains(id);        
    }

    

    @Override
    public long getNumberOfRecords() throws DataStoreException{
        return traceArchiveInfo.getNumberOfRecords();
       
    }


    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return traceArchiveInfo.idIterator();
    }
    
    

    @Override
    public StreamingIterator<TraceArchiveTrace> iterator() {
        return new DataStoreIterator<TraceArchiveTrace>(this);
    }

    protected abstract TraceArchiveTrace createTraceArchiveTrace(String id)
                                                throws DataStoreException;
}