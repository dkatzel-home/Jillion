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
 * Created on Jan 25, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
/**
 * {@code DefaultPhdDataStoreFactory} is an implementation
 * of {@link PhdDataStoreFactory} that creates {@link DefaultPhdFileDataStore}s.
 * @author dkatzel
 *
 *
 */
public class DefaultPhdFileDataStoreFactory implements PhdDataStoreFactory {

    @Override
    public PhdDataStore createPhdDataStoreFactoryFor(File phdBall)
            throws DataStoreException {
        DefaultPhdFileDataStore datastore = new DefaultPhdFileDataStore();
        FileInputStream in=null;
        try {
            in = new FileInputStream(phdBall);
            PhdParser.parsePhd(in, datastore);
            return datastore;
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not parse phd ball", e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        
    }

}
