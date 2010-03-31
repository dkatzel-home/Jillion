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

package org.jcvi.assembly.cas;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * {@code DefaultTrimFileCasTrimMap} is an
 * implementation of {@link CasTrimMap}
 * that reads a trim map file to get the mapping information.
 * @author dkatzel
 *
 *
 */
public class DefaultTrimFileCasTrimMap implements CasTrimMap{
    Map<File, File> fileMap = new HashMap<File, File>();
    /**
     * Constructs a new {@link CasTrimMap} containing the data
     * provided by the given trim map file.
     * @param trimMapFile the trim map {@link File}.
     * @throws FileNotFoundException if the given trim map file does not exist.
     */
    public DefaultTrimFileCasTrimMap(File trimMapFile) throws FileNotFoundException{
        Map<String, String> pathMap= CasConversionUtil.parseTrimmedToUntrimmedFiles(trimMapFile);
        for(Entry<String, String> file : pathMap.entrySet()){
            fileMap.put(new File(file.getKey()), new File(file.getValue()));
        }
    }
    @Override
    public File getUntrimmedFileFor(File pathtoTrimmedDataStore){
        if(fileMap.containsKey(pathtoTrimmedDataStore)){
            return fileMap.get(pathtoTrimmedDataStore);
        }
        return pathtoTrimmedDataStore;
    }
}
