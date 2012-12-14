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
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fasta.nt.DefaultNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideDataStoreFastaAdatper extends AbstractTestSequenceFastaDataStoreWithNoComment{

    @Override
    protected DataStore<NucleotideSequenceFastaRecord> createDataStore(
            File file) throws IOException {
        return DefaultNucleotideSequenceFastaFileDataStore.create(file);
    }

    @Test
    public void adaptFasta() throws IOException, DataStoreException{
        NucleotideSequenceDataStore sut=
        		FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, createDataStore(
        		RESOURCES.getFile(FASTA_FILE_PATH)));
    
        assertEquals(
                sut.get("hrv-61"), hrv_61.getSequence());
    }
}