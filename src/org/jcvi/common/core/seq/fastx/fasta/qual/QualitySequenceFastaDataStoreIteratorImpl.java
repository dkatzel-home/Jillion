package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreClosedException;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;

class QualitySequenceFastaDataStoreIteratorImpl extends AbstractBlockingCloseableIterator<QualitySequenceFastaRecord>{
		private final File fastaFile;
		public QualitySequenceFastaDataStoreIteratorImpl(File fastaFile) {
			if(!fastaFile.exists()){
				throw new IllegalArgumentException("fasta file must exist");
			}
			this.fastaFile = fastaFile;
		}
		/**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	        FastaFileVisitor visitor = new AbstractFastaVisitor(){
	        	/**
	    	     * @throws DataStoreClosedException if 
	    	     * this {@link DataStore} which backs
	    	     * this iterator is closed.
	    	     */
				@Override
				protected boolean visitRecord(String id, String comment,
						String entireBody) {
					QualitySequenceFastaRecord record = 
							QualitySequenceFastaRecordFactory.create(id, entireBody, comment);
					blockingPut(record);
					return true;
				}
	        	
	        };
	        try {
	            FastaFileParser.parse(fastaFile, visitor);
	        } catch (FileNotFoundException e) {
	            throw new RuntimeException("fasta file does not exist",e);
	        }
	        
	

	    }
	    
	    
}