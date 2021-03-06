/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableStreamingIterator;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code ReSortSamFileWriter}
 * is a {@link SamWriter} implementation
 * that can take {@link SamRecord}s given to it via
 * the {@link SamWriter#writeRecord(SamRecord)}
 * in ANY ORDER and write out the SAM or BAM file
 * sorted by the specified manner.
 * Subclasses handle the actual SAM/BAM encoding.
 * <p>
 * Algorithm: Keep an array of records in memory
 * (size of array specified by user).  When we
 * have filled the array, sort the in memory records
 * and write out a SAM/BAM file to a temp file and
 * clear the in memory array to make room for more
 * records.
 * When {@link SamWriter#close()} is called,
 * write the combined sorted
 * records to the specified output file.
 * Since 
 * we know each temp file and the in memory array are 
 * each sorted, we can use a merge sort like algorithm
 * to combine the records into one giant sorted list. 
 * </p>
 * @author dkatzel
 *
 */
class ReSortSamFileWriter implements SamWriter {

	private final SamHeader header;
	private final Comparator<SamRecord> recordComparator;
	private final File tmpDir;
	private final File outputFile;
	
	private SamRecord[] inMemoryArray;
	private int currentInMemSize;
	private final int maxRecordsToKeepInMemory;
	private final List<File> tempFiles = new ArrayList<File>();
	private final SamAttributeValidator attributeValidator;
	
	private final Encoding encoding;
	private final BamIndexer indexer;
	private final boolean includeIndexMetaData;
	
	
	/**
	 * 
	 * @param outputFile The output File to write.
	 * @param tmpDirRoot the root directory to create child temp directories to write temp data to.
	 * @param header the {@link SamHeader} to use in the output file.
	 * @param maxRecordsToKeepInMemory the max number of SamRecords to keep in memory at a time.  When 
	 * more than this amount of records have been written to memory, they will be flushed to a temp file under the tmpDirRoot.
	 * Value must be &ge; 0.
	 * 
	 * @param attributeValidator the {@link SamAttributeValidator} to use to validate the records to be written;
	 * can not be null.
	 * 
	 * @param encodingToUse The {@link Encoding} to use to write the output file; can not be null.
	 * @param indexer the {@link BamIndexer} to use to index the files
	 * @param includeIndexMetaData  should the index also include metadata.
	 * 
	 * @throws NullPointerException if any of the parameters that can't be null are null.
	 * @throws IllegalArgumentException if maxRecordsToKeepInMemory is negative.
	 * 
	 * @throws IOException if there is a problem creating the output file or creating a temp directory under tmpDirRoot.
	 */
	ReSortSamFileWriter(File outputFile, File tmpDirRoot, SamHeader header, 
	        int maxRecordsToKeepInMemory, SamAttributeValidator attributeValidator,
	        Encoding encodingToUse, BamIndexer indexer, boolean includeIndexMetaData) throws IOException {
		
		if(maxRecordsToKeepInMemory <0){
			throw new IllegalArgumentException("max records to keep in memory must be >=1");
		}
		if(attributeValidator ==null){
			throw new NullPointerException("attribute valiator can not be null");
		}
		if(encodingToUse ==null){
			throw new NullPointerException("Encoding can not be null");
		}
		this.maxRecordsToKeepInMemory = maxRecordsToKeepInMemory;
		
		this.header = header;
		recordComparator = createRecordComparatorFor(header);
		if(recordComparator ==null){
			throw new NullPointerException("SortOrder must create a non-null comparator " + header.getSortOrder());
		}
		
		IOUtil.mkdirs(outputFile.getParentFile());
		tmpDir = IOUtil.createTempDir("jillion", "samWriterTmp", tmpDirRoot);		
        inMemoryArray = new SamRecord[maxRecordsToKeepInMemory];
        currentInMemSize=0;
        this.outputFile = outputFile;
        this.attributeValidator = attributeValidator;
        this.encoding = encodingToUse;
        this.indexer = indexer;
        
        this.includeIndexMetaData = includeIndexMetaData;
	}


	 private static Comparator<SamRecord> createRecordComparatorFor(SamHeader header){
	     SortOrder sortOrder = header.getSortOrder();
             if(sortOrder ==null){
                     return null;
             }
             return sortOrder.createComparator(header);
     }
	
	@Override
	public void writeRecord(SamRecord record) throws IOException {
		if(record==null){
			throw new NullPointerException("record can not be null");
		}
		persistInMemoryCacheIfNeeded();
		try{
			header.validateRecord(record, attributeValidator);
		}catch(SamValidationException e){
			throw new IOException("can not write record due to validation error(s)",e);
		}
		
		inMemoryArray[currentInMemSize++] = record;
	}

	/**
	 * Write all the records currently in memory
	 * to a temp file encoded using the given encoding
	 * @throws IOException
	 */
	private void persistInMemoryCacheIfNeeded() throws IOException {
		if(currentInMemSize == maxRecordsToKeepInMemory){
			
			sortInMemoryRecords();
			
			File tempFile= File.createTempFile(outputFile.getName(), encoding.getSuffix(), tmpDir);
			tempFiles.add(tempFile);
			//never pass indexer to temp files
			//only use in final if at all.
			SamWriter writer = encoding.createPreSortedNoValidationOutputWriter(tempFile, header,null, false);
			try{
				for(int i=0; i<currentInMemSize; i++){
					writer.writeRecord(inMemoryArray[i]);
				}
			}finally{
				IOUtil.closeAndIgnoreErrors(writer);
				clearInMemoryArray();
			}
			
		}
		
	}


	private void clearInMemoryArray() {
		currentInMemSize=0;
		//clear references to free up GC
		Arrays.fill(inMemoryArray, null);
	}


	private void sortInMemoryRecords() {
		//sort records while they are still in memory
		//our comparators in SortOrder
		//handle nulls and sort them last
		//so we don't have to worry about those
		//throwing NPE.
		Arrays.sort(inMemoryArray, recordComparator);
	}


	
	
	@Override
	public void close() throws IOException {
		List<PeekableStreamingIterator<SamRecord>> iterators =new ArrayList<PeekableStreamingIterator<SamRecord>>(1 + tempFiles.size());
		SamWriter writer =null;
		try{
			sortInMemoryRecords();

			iterators.add(IteratorUtil.createPeekableStreamingIterator(new InMemoryStreamingIterator(currentInMemSize)));
			
			for(File tempFile : tempFiles){
				iterators.add(IteratorUtil.createPeekableStreamingIterator(new StreamingSamRecordIterator(tempFile, encoding)));
			}
			
			Iterator<SamRecord> sortedIterator = new MergedSortedRecordIterator(iterators, recordComparator);
			writer = encoding.createPreSortedNoValidationOutputWriter(outputFile, header, indexer, includeIndexMetaData);
			while(sortedIterator.hasNext()){
				writer.writeRecord(sortedIterator.next());
			}
		}finally{
			for(StreamingIterator<?> iter : iterators){
				IOUtil.closeAndIgnoreErrors(iter);
			}
			IOUtil.closeAndIgnoreErrors(writer);
			clearInMemoryArray();
			//delete temp dir
			IOUtil.recursiveDelete(tmpDir);
		}
		
	}


	

	

	private final class InMemoryStreamingIterator implements StreamingIterator<SamRecord>{

		private final int length;
		private int counter=0;
		
		InMemoryStreamingIterator(int numOfInMemoryRecords){
			this.length = numOfInMemoryRecords;
		}
		
		@Override
		public boolean hasNext() {
			return counter<length;
		}

		@Override
		public void close() {
			//no-op
			
		}

		@Override
		public SamRecord next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			return inMemoryArray[counter++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}
	/**
	 * Combine a list of pre-sorted Iterators into a single sorted iterator.
	 * Each call to {@link #next()} will peek at the next elements in the wrapped
	 * iterators and return the value that has the lowest sort value as determined
	 * by the comparator (and advance that iterator).
	 * @author dkatzel
	 *
	 */
	public static class MergedSortedRecordIterator implements Iterator<SamRecord> {
			private final List<PeekableStreamingIterator<SamRecord>> iterators;
			
			private SamRecord next;
			private final SortedSamRecordElementComparator comparator;
			private final List<SortedSamRecordElement> elementList;
			
			
			public MergedSortedRecordIterator(List<PeekableStreamingIterator<SamRecord>> iterators, Comparator<SamRecord> comparator) {
				this.iterators = iterators;
				this.comparator = new SortedSamRecordElementComparator(comparator);
				elementList = new ArrayList<SortedSamRecordElement>(iterators.size());
				
				next= getNext();
			}
			
			private SamRecord getNext(){
				elementList.clear();
				for(PeekableStreamingIterator<SamRecord> iter : iterators){
					if(iter.hasNext()){
						//we peek instead of next()
						//incase we don't pick this record yet
						elementList.add(new SortedSamRecordElement(iter.peek(), iter));
					}
				}
				if(elementList.isEmpty()){
					return null;
				}
				Collections.sort(elementList, comparator);
				SortedSamRecordElement element= elementList.get(0);
				//advance iterator
				element.source.next();
				return element.record;
			}

			@Override
			public boolean hasNext() {
				return next!=null;
			}
			
			
			@Override
			public SamRecord next() {
				//don't need to check has next
				//since we can make sure we don't call it incorrectly
				SamRecord ret= next;
				next = getNext();
				return ret;
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();				
			}

	}
	/**
	 * Struct that has a {@link SamRecord} and which
	 * iterator that record belong to so we can advance
	 * the iterator if selected.
	 * @author dkatzel
	 *
	 */
	private static class SortedSamRecordElement{
		SamRecord record;
		Iterator<SamRecord> source;
		
		public SortedSamRecordElement(SamRecord record,
				Iterator<SamRecord> source) {
			this.record = record;
			this.source = source;
		}

		@Override
		public String toString() {
			return "SortedSamRecordElement [record=" + record + ", source="
					+ source + "]";
		}
		
	}
	
	private static class SortedSamRecordElementComparator implements Comparator<SortedSamRecordElement>{
		private final Comparator<SamRecord> comparator;
		

		public SortedSamRecordElementComparator(Comparator<SamRecord> comparator) {
			this.comparator = comparator;
		}


		@Override
		public int compare(SortedSamRecordElement o1, SortedSamRecordElement o2) {
			return comparator.compare(o1.record, o2.record);
		}
		
	}
	/**
	 * Iterates over a sam (or bam) encoded file as a {@link StreamingIterator}.
	 * @author dkatzel
	 *
	 */
	private static class StreamingSamRecordIterator extends AbstractBlockingStreamingIterator<SamRecord>{

		private final File samFile;
		private final Encoding encoding;
		
		public StreamingSamRecordIterator(File samFile, Encoding encoding) {
			this.samFile = samFile;
			this.encoding = encoding;
			this.start();
		}


		@Override
		protected void backgroundThreadRunMethod() throws RuntimeException {
			try {
				encoding.createNewNoValidationSamParser(samFile).parse(new SamVisitor() {
					
					
					@Override
					public void visitRecord(SamVisitorCallback callback, SamRecord record,
							VirtualFileOffset start, VirtualFileOffset end) {
						StreamingSamRecordIterator.this.blockingPut(record);
						
					}
					
					
					@Override
					public void visitHeader(SamVisitorCallback callback, SamHeader header) {
						//no-op
						
					}
					
					@Override
					public void visitEnd() {
						//no-op
						
					}
					@Override
					public void halted() {
						
					}



					
				});
			} catch (IOException e) {
				throw new RuntimeException("error parsing temp sam file " + samFile.getAbsolutePath(), e);
			}
			
		}
		
	}

}
