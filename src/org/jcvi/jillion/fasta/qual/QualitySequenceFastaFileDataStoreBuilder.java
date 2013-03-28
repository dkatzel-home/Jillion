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
package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.internal.fasta.AbstractFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.fasta.qual.IndexedQualityFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.qual.LargeQualityFastaFileDataStore;

/**
 * {@code QualitySequenceFastaFileDataStoreFactory}
 * is a factory class that can create new instances
 * of {@link QualitySequenceFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class QualitySequenceFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>{

	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public QualitySequenceFastaFileDataStoreBuilder(File fastaFile)
			throws IOException {
		super(fastaFile);
	}

	
	@Override
	protected QualitySequenceFastaDataStore createNewInstance(File fastaFile,
			DataStoreProviderHint hint, DataStoreFilter filter)
			throws IOException {
		switch(hint){
			case OPTIMIZE_FAST_RANDOM_ACCESS: return DefaultQualityFastaFileDataStore.create(fastaFile,filter);
			case OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS: return IndexedQualityFastaFileDataStore.create(fastaFile,filter);
			case ITERATION_ONLY: return LargeQualityFastaFileDataStore.create(fastaFile,filter);
			default:
				throw new IllegalArgumentException("unknown hint : "+ hint);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public QualitySequenceFastaFileDataStoreBuilder filter(
			DataStoreFilter filter) {
		super.filter(filter);
		return this;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public QualitySequenceFastaFileDataStoreBuilder hint(
			DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public QualitySequenceFastaDataStore build() throws IOException {
		return super.build();
	}
	
	
}
