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
package org.jcvi.jillion.trace.sff;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
/**
 * 454 includes an optional index at the 
 * end of their {@literal .sff} files that contains
 * and encoded file offset for each record
 * in the file.  The actual format is not 
 * specified in the 454 manual or file specification
 * so it had to be reverse engineered.  This class
 * can parse the optional index to get the 
 * file offsets for each read without having
 * to parse the entire file (so it is much
 * faster to create a DataStore instance than
 * {@link CompletelyParsedIndexedSffFileDataStore} which
 * does have to parse the entire file.
 * <p/>
 * This class supports two index encodings.
 * <ol>
 * <li>An XML manifest and sorted index that are generated
 * by 454 programs by default.</li>
 * <li>Just a sorted index without the XML manifest 
 * that can be generated by sfffile using the "-nmft" option.</li>
 * </ol>
 * This class does not work on ion torrent sff files
 * since as of Spring 2012, they don't include an index.
 * @author dkatzel
 *
 */
final class ManifestIndexed454SffFileDataStore implements SffFileDataStore{
	
	private final RandomAccessFile randomAccessFile;
	private final File sffFile;
	private final SffCommonHeader commonHeader;
	/**
	 * It appears that 454 will
	 * only make an index if the file size <4GB
	 * so we can use unsigned ints to save memory
	 * in the index.
	 */
	private final Map<String, Integer> map;
	private boolean isClosed=false;
	private final DataStoreFilter filter;
	/**
	 * Try to create a {@link SffFileDataStore} by only parsing
	 * the 454 index at the end of the sff file.
	 * If there is no index or it is encoded
	 * in an unknown format, then this method will
	 * return null.
	 * @param sffFile
	 * @return an {@link SffFileDataStore} if successfully
	 * parsed; or {@code null} if the index can't
	 * be parsed.
	 * @throws IOException if there is a problem reading the file.
	 */
	public static SffFileDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Try to create a {@link SffFileDataStore} by only parsing
	 * the 454 index at the end of the sff file.
	 * If there is no index or it is encoded
	 * in an unknown format, then this method will
	 * return null.
	 * @param sffFile
	 * @param filter
	 * @return an {@link SffFileDataStore} if successfully
	 * parsed; or {@code null} if the index can't
	 * be parsed.
	 * @throws IOException if there is a problem reading the file.
	 */
	public static SffFileDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		ManifestCreatorVisitor visitor = new ManifestCreatorVisitor(sffFile, filter);
		SffFileParser.create(sffFile).parse(visitor);
		//there is a valid sff formatted manifest inside the sff file
		if(visitor.isUseableManifest()){
			return new ManifestIndexed454SffFileDataStore(visitor);
		}
		//no manifest delegate to iterating thru
		return null;
		
	}
	
	
	private ManifestIndexed454SffFileDataStore(ManifestCreatorVisitor visitor) throws FileNotFoundException{
		this.map = visitor.map;
		this.sffFile =visitor.sffFile;
		this.randomAccessFile = new RandomAccessFile(visitor.sffFile, "r");
		this.commonHeader = visitor.commonHeader;
		this.filter = visitor.filter;
	}
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		throwErrorIfClosed();
		try {
			//use large sffFileDataStore 
			//to parse ids in order in file
			return LargeSffFileDataStore.create(sffFile,filter).idIterator();
		} catch (IOException e) {
			throw new IllegalStateException("sff file has been deleted",e);
		}
	}

	@Override
	public SffFlowgram get(String id) throws DataStoreException {
		throwErrorIfClosed();
		Long offset = getOffsetFor(id);
		if(offset ==null){
			return null;
		}
		
		InputStream in=null;
		try {
			//need to synchronize on the random access file
			//since we seek and read from it
			synchronized(randomAccessFile){
				randomAccessFile.seek(offset);
				in = new BufferedInputStream(new RandomAccessFileInputStream(randomAccessFile));
				DataInputStream dataIn = new DataInputStream(in);
				 SffReadHeader readHeader = DefaultSffReadHeaderDecoder.INSTANCE.decodeReadHeader(dataIn);
				 final int numberOfBases = readHeader.getNumberOfBases();
	             SffReadData readData = DefaultSffReadDataDecoder.INSTANCE.decode(dataIn,
	                             commonHeader.getNumberOfFlowsPerRead(),
	                             numberOfBases);
	             return SffFlowgramImpl.create(readHeader, readData);
			}
		} catch (IOException e) {
			throw new DataStoreException("error trying to get flowgram "+ id,e);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	/**
	 * Get the offset into the sff file from the manifest.
	 * @param id the read id to get.
	 * @return Returns offset into sff file which contains the start
	 * of the given record or null if there is no read
	 * with that name in the manifest.
	 */
	private Long getOffsetFor(String id){
		Integer offset= map.get(id);
		if(offset ==null){
			return null;
		}
		return IOUtil.toUnsignedInt(offset.intValue());
	}
	@Override
	public boolean contains(String id) throws DataStoreException {
		throwErrorIfClosed();
		return map.containsKey(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		throwErrorIfClosed();
		return map.size();
	}

	@Override
	public NucleotideSequence getKeySequence() {
		throwErrorIfClosed();
		return commonHeader.getKeySequence();
	}
	@Override
	public NucleotideSequence getFlowSequence() {
		throwErrorIfClosed();
		return commonHeader.getFlowSequence();
	}
	@Override
	public synchronized boolean isClosed(){
		return isClosed;
	}

	@Override
	public StreamingIterator<SffFlowgram> iterator() throws DataStoreException {
		throwErrorIfClosed();
		try {
			return LargeSffFileDataStore.create(sffFile,filter).iterator();
		} catch (IOException e) {
			throw new DataStoreException("sff file has been deleted",e);
		}
	}

	@Override
	public synchronized  void close() throws IOException {
		isClosed =true;
		map.clear();
		randomAccessFile.close();
	}
	
	private synchronized void throwErrorIfClosed(){
		if(isClosed){
			throw new DataStoreClosedException("closed");
		}
	}

	private static final class ManifestCreatorVisitor implements SffVisitor{
		/**
		 * 454 sff encoded names that follow the 454 spec
		 * should only be 14 characters long.
		 */
		private static final int INITIAL_NAME_SIZE = 14;

		
		private final File sffFile;
		private SffCommonHeader commonHeader;
		private Map<String, Integer> map;
		private boolean useableManifest=false;
		
		private final DataStoreFilter filter;
		
		private ManifestCreatorVisitor(File sffFile, DataStoreFilter filter) {
			this.sffFile = sffFile;
			this.filter = filter;
			
		}
		

		public boolean isUseableManifest() {
			return useableManifest;
		}
		

		@Override
		public void visitHeader(SffVisitorCallback callback,
				SffCommonHeader header) {
			this.commonHeader = header;
			BigInteger offsetToIndex =commonHeader.getIndexOffset();
			if(offsetToIndex.longValue() !=0L){
				tryToParseManifest(offsetToIndex);
			}
			callback.haltParsing();
		}
		@Override
		public SffFileReadVisitor visitRead(SffVisitorCallback callback,
				SffReadHeader readHeader) {
			//should never get this far but skip just in case.
			return null;
		}
		@Override
		public void end() {
			//no-op			
		}
		

		private void tryToParseManifest(BigInteger offsetToIndex) {
			InputStream in=null;
			try {
				in=new BufferedInputStream(new RandomAccessFileInputStream(sffFile, offsetToIndex.longValue()));
				
			    //pseudocode:
				//skip xml manifest if present
				//read bytes until byte value is 0
				//anything before the 0 is the read name
				//next 4 bytes? is offset into file using base 255
				//offset also appears to be in little endian
				//another null byte separator before next entry

				byte[] magicNumber =new byte[4];
				IOUtil.blockingRead(in, magicNumber);
				if(Arrays.equals(magicNumber, ".mft".getBytes(IOUtil.UTF_8))){
					//includes xml plus sorted index
					byte[] version = new byte[4];
					IOUtil.blockingRead(in, version);
					String versionString = new String(version, IOUtil.UTF_8);
					if(!"1.00".equals(versionString)){
						throw new IOException("unsupported xml manifest version : " + versionString);
					}
					long xmlLength =IOUtil.readUnsignedInt(in);
					//skip 4 bytes for the datalength
					IOUtil.blockingSkip(in, 4 + xmlLength);
					populateOffsetMap(in); 
					useableManifest=true;
				}
				//this kind of index is created
				//by sfffile if you say "no manifest"
				if(Arrays.equals(magicNumber, ".srt".getBytes(IOUtil.UTF_8))){
					//includes xml plus sorted index
					byte[] version = new byte[4];
					IOUtil.blockingRead(in, version);
					String versionString = new String(version, IOUtil.UTF_8);
					if(!"1.00".equals(versionString)){
						throw new IOException("unsupported sorted manifest version : " + versionString);
					}
					
					
					populateOffsetMap(in); 
					useableManifest=true;
				}
			
			}catch (FileNotFoundException e1) {
				//this shouldn't happen under normal circumstances since 
				//in order to get this far we had the file has to exist.
				throw new RuntimeException("the sff file no longer exists", e1);
			}catch (IOException e) {
				throw new RuntimeException("error parsing manifest", e);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
		}


		
		private void populateOffsetMap(InputStream in) throws IOException {
			int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(commonHeader.getNumberOfReads());
			map = new HashMap<String, Integer>(mapSize);
			for(long i =0; i< commonHeader.getNumberOfReads(); i++){
				String id = parseNextId(in);
				if(id ==null){
					throw new IOException(
							String.format("incomplete index in sff file; missing %d reads",commonHeader.getNumberOfReads()-i));
				}
				byte[] index = new byte[4];
				IOUtil.blockingRead(in, index);
				//only include id in index if we care about it.
				if(filter.accept(id)){
					long offset =SffUtil.parseSffIndexOffsetValue(index);
					//signed int to save space
					map.put(id,IOUtil.toSignedInt(offset));
				}
				//next byte is a separator
				//between entries so we can skip it
				in.read();
			}
		}

		private String parseNextId(InputStream in) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream(INITIAL_NAME_SIZE);
			byte nextByte =(byte)in.read();
			if(nextByte==-1){
				return null;
			}
			do{
				out.write(nextByte);
				nextByte =(byte)in.read();
			}while(nextByte !=0);
			return new String(out.toByteArray(), IOUtil.UTF_8);
		}
	}
}
