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
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.datastore.EmptyDataStoreFilter;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public abstract class AbstractH2FastQDataStore<G extends Glyph, E extends EncodedGlyphs<G>> implements DataStore<E>, FastQFileVisitor{
    private final AbstractH2EncodedGlyphDataStore<G, E> datastore;
    private final FastQQualityCodec qualityCodec;
    private final DataStoreFilter filter;
    
    private String currentId;
    public AbstractH2FastQDataStore(File fastQFile,FastQQualityCodec qualityCodec,AbstractH2EncodedGlyphDataStore<G, E> datastore,DataStoreFilter filter) throws FileNotFoundException {
        this.datastore = datastore;
        this.qualityCodec = qualityCodec;
        this.filter= filter;
        FastQFileParser.parse(fastQFile, this);
    }
    public AbstractH2FastQDataStore(File fastQFile,FastQQualityCodec qualityCodec,AbstractH2EncodedGlyphDataStore<G, E> datastore) throws FileNotFoundException {
        this(fastQFile,qualityCodec,datastore, EmptyDataStoreFilter.INSTANCE);
    }
    public AbstractH2EncodedGlyphDataStore<G, E> getDatastore() {
        return datastore;
    }

    public FastQQualityCodec getQualityCodec() {
        return qualityCodec;
    }

    public String getCurrentId() {
        return currentId;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }
    @Override
    public E get(String id) throws DataStoreException {
        return datastore.get(id);
    }
    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }
    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }
    @Override
    public void close() throws IOException {
        datastore.close();
        
    }
    @Override
    public Iterator<E> iterator() {
        return datastore.iterator();
    }

    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        currentId = id;
        return filter.accept(id);
    }


    @Override
    public void visitEndBlock() {
    }


    @Override
    public void visitLine(String line) {
        
    }

    @Override
    public void visitEndOfFile() {
        
    }

    @Override
    public void visitFile() {
        
    }
    @Override
    public void visitEncodedQualities(String encodedQualities) {
    }

    @Override
    public void visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
    }
    
}
