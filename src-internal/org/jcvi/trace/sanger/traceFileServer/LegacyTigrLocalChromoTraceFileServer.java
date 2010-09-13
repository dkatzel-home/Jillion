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
 * Created on Aug 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.jcvi.util.FileIterator;
import org.jcvi.util.StringUtilities;
/**
 * {@code LegacyTigrLocalChromoTraceFileServer} is a {@link TraceFileServer}
 * implementation that can read a legacy TIGR "local chromo" directory.
 * @author dkatzel
 *
 *
 */
public class LegacyTigrLocalChromoTraceFileServer implements TraceFileServer, Iterable<String> {

    private static final List<String> SUPPORTED_SUFFIXES = Arrays.asList(".ztr",".gz");
    private final String baseDir;
    public LegacyTigrLocalChromoTraceFileServer(String localChromoPath,String tigrProject){
        baseDir = localChromoPath + "/"+tigrProject.toUpperCase() + "/ABISSed";
    }
    
    protected String generateLocalChromoPathFor(String seqName){
        
        return new StringUtilities.JoinedStringBuilder(
                baseDir, 
                seqName.substring(0, 3),
                seqName.substring(0, 4),
                seqName.substring(0, 5),
                seqName)
                .glue('/')
                .build();
       
    }
    private File getFileFor(String seqName){
        String expectedPath = generateLocalChromoPathFor(seqName);
        for(String extension : SUPPORTED_SUFFIXES){
            File f = new File(expectedPath + extension);
            if(f.exists()){
                return f;
            }
        }
        return null;
        
    }
    @Override
    public boolean contains(String seqName) throws IOException {
        return getFileFor(seqName) !=null;
    }

    @Override
    public File getFile(String seqName) throws IOException {
        File f= getFileFor(seqName);
        if(f==null){
            throw new IOException("trace file for "+ seqName + " does not exist");
        }
        return f;
    }

    @Override
    public InputStream getFileAsStream(String seqName) throws IOException {
        final File f = getFile(seqName);
        final FileInputStream fileInputStream = new FileInputStream(f);
        if(f.getName().endsWith(".gz")){            
            return new ZipInputStream(fileInputStream);
        }
        return fileInputStream;
    }

    @Override
    public boolean supportsGettingFileObjects() {
        return true;
    }

    @Override
    public void close() throws IOException {
        //no-op
    }

    @Override
    public Iterator<String> iterator() {
        return new SeqNameFileIterator(
                FileIterator.createDepthFirstFileIteratorBuilder(new File(baseDir)).build());
    }

    

    private String getSeqnameFor(File file) {
        String nameWithSuffix = file.getName();
        String nameWithoutSuffix = nameWithSuffix.replaceAll("\\..+?$", "");
        return nameWithoutSuffix;
    }

    private final class SeqNameFileIterator implements Iterator<String>{
        private final Iterator<File> fileIterator;
        private SeqNameFileIterator(Iterator<File> fileIterator){
            this.fileIterator = fileIterator;
        }
        @Override
        public boolean hasNext() {
            return fileIterator.hasNext();
        }

        @Override
        public String next() {
            File file = fileIterator.next();
            return getSeqnameFor(file);
        }

        @Override
        public void remove() {
            fileIterator.remove();
            
        }
        
    }
    
}
