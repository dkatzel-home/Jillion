/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta;

import java.io.IOException;
import java.util.Map;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.fasta.FastaRecord;
/**
 * Wrapper class that converts a Map of FastaRecords or a plain DataStore 
 * of FastaRecords into a {@link FastaDataStore} object.  This lets
 * proxy classes work with FastaDataStore specific methods that
 * aren't normally accessible with a plain DataStore object.
 * 
 * @author dkatzel
 *
 * @param <S> the type of element in the sequence
 * @param <T> the type of Sequence in the fasta record
 * @param <F> the type of {@link FastaRecord} in the datastore.
 * 
 * @since 5.1
 */
public abstract class AdaptedFastaDataStore<S,  T extends Sequence<S>, F extends FastaRecord<S,T>, D extends DataStore<T>> implements FastaDataStore<S, T, F, D>{

	private final DataStore<F> delegate;
	
	
	public AdaptedFastaDataStore(Map<String, F> map) {
		this(DataStore.of(map));
	}
	public AdaptedFastaDataStore(DataStore<F> delegate) {
		this.delegate = delegate;
	}

	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return delegate.idIterator();
	}

	@Override
	public F get(String id) throws DataStoreException {
		return delegate.get(id);
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		return delegate.contains(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		return delegate.getNumberOfRecords();
	}

	@Override
	public boolean isClosed() {
		return delegate.isClosed();
	}

	@Override
	public StreamingIterator<F> iterator() throws DataStoreException {
		return delegate.iterator();
	}

	@Override
	public StreamingIterator<DataStoreEntry<F>> entryIterator() throws DataStoreException {
		return delegate.entryIterator();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}
    @Override
    public abstract D asSequenceDataStore();

}
