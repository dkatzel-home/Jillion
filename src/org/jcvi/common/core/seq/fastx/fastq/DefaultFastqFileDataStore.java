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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.seq.fastx.AcceptingFastXFilter;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code DefaultFastqFileDataStore} is the default implementation
 * of {@link FastqDataStore} which stores
 * all {@link FastqRecord}s from a file in memory.  This is only recommended for small fastq
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeFastqFileDataStore
 *
 */
public final class DefaultFastqFileDataStore{

   /**
    * Create a new {@link FastqFileDataStoreBuilderVisitor} instance
    * that needs to be populated by passing it to either {@link FastqFileParser#parse(File, FastqFileVisitor)}
    * or {@link FastqFileParser#parse(InputStream, FastqFileVisitor)}.
    * @param qualityCodec the {@link FastqQualityCodec} needed
    * to parse the encoded quality values in each record;
    * can not be null.
    * @return a new {@link FastqFileDataStoreBuilderVisitor} instance;
    * never null.
    * @throws NullPointerException if qualityCodec is null.
    */
   public static FastqFileDataStoreBuilderVisitor createBuilder(FastqQualityCodec qualityCodec){
	   return createBuilder(qualityCodec, AcceptingFastXFilter.INSTANCE);
   }
   /**
    * Create a new {@link FastqFileDataStoreBuilderVisitor} instance
    * that needs to be populated by passing it to either {@link FastqFileParser#parse(File, FastqFileVisitor)}
    * or {@link FastqFileParser#parse(InputStream, FastqFileVisitor)}.
    * @param qualityCodec the {@link FastqQualityCodec} needed
    * to parse the encoded quality values in each record;
    * can not be null.
    * @param filter an instance of {@link FastXFilter} that can
    * be used to filter out some {@link FastqRecord}s from
    * the datastore; can not be null
    * @return a new {@link FastqFileDataStoreBuilderVisitor} instance;
    * never null.
    * @throws NullPointerException if either filter or qualityCodec is null.
    */
   public static FastqFileDataStoreBuilderVisitor createBuilder(FastqQualityCodec qualityCodec, FastXFilter filter){
	   return new DefaultFastqFileDataStoreBuilderVisitor(qualityCodec, filter);
   }
   /**
    * Create a new {@link FastqDataStore} instance
    * for all the {@link FastqRecord}s that are contained
    * in the given fastq file.  All records in the file
    * must have their qualities encoded a manner 
    * that can be parsed by the given {@link FastqQualityCodec}.
    * @param fastqFile the fastq file to parse, must exist
    * and can not be null.
    * @param qualityCodec the {@link FastqQualityCodec} needed
    * to parse the encoded quality values in each record;
    * can not be null.
    * @return a new {@link FastqDataStore} instance.
    * @throws IOException if there is a problem parsing the fastq file.
    * @throws NullPointerException if either fastqFile or qualityCodec is null.
    */
   public static FastqDataStore create(File fastqFile, FastqQualityCodec qualityCodec) throws IOException{
	   FastqFileDataStoreBuilderVisitor builderVisitor = createBuilder(qualityCodec);
	   FastqFileParser.parse(fastqFile, builderVisitor);
	   return builderVisitor.build();
   }
   /**
    * Create a new {@link FastqDataStore} instance
    * for the {@link FastqRecord}s that are accepted 
    * by the given {@link FastXFilter} that are contained
    * in the given fastq file.  Any records that are not
    * accepted by the filter will not be included
    * in the returned {@link FastqDataStore}. All of those records 
    * must have their qualities encoded a manner 
    * that can be parsed by the given {@link FastqQualityCodec}.
    * @param fastqFile the fastq file to parse, must exist
    * and can not be null.
    * @param qualityCodec the {@link FastqQualityCodec} needed
    * to parse the encoded quality values in each record;
    * can not be null.
    * @param filter an instance of {@link FastXFilter} that can
    * be used to filter out some {@link FastqRecord}s from
    * the datastore.
    * @return a new {@link FastqDataStore} instance containing
    * only those records that pass the filter.
 * @throws IOException if thre is a problem parsing the fastq file.
    * @throws NullPointerException if either fastqFile, qualityCodec or filter is null.
    */
   public static FastqDataStore create(File fastqFile, FastqQualityCodec qualityCodec,FastXFilter filter) throws IOException{
	   FastqFileDataStoreBuilderVisitor builderVisitor = createBuilder(qualityCodec, filter);
	   FastqFileParser.parse(fastqFile, builderVisitor);
	   return builderVisitor.build();
   }
   /**
    * Create a new {@link FastqDataStore} instance
    * for all the {@link FastqRecord}s that are contained
    * in the given {@link InputStream} containing fastq encoded data.
    * <strong>NOTE: </strong>After parsing, the given stream will still be open!
    * It is up to the client to close the stream.  All records in the file
    * must have their qualities encoded a manner 
    * that can be parsed by the given {@link FastqQualityCodec}.
    * @param fastqStream an {@link InputStream} instance containing
    * the fastq data to parse.  This stream will not be automatically
    * closed; client code must close their stream on their own.
    * @param qualityCodec the {@link FastqQualityCodec} needed
    * to parse the encoded quality values in each record;
    * can not be null.
    * @return a new {@link FastqDataStore} instance.
    * @throws IOException if there is a problem parsing the fastq stream.
    * @throws NullPointerException if either fastqStream or qualityCodec is null. 
    */
   public static FastqDataStore create(InputStream fastqStream, FastqQualityCodec qualityCodec) throws IOException{
	   FastqFileDataStoreBuilderVisitor builderVisitor = createBuilder(qualityCodec);
	   FastqFileParser.parse(fastqStream, builderVisitor);
	   return builderVisitor.build();
   }
   /**
    * Create a new {@link FastqDataStore} instance
    * for the {@link FastqRecord}s that are accepted 
    * by the given {@link FastXFilter} that are contained
    * in the given {@link InputStream} containing fastq encoded data.
    *   Any records that are not
    * accepted by the filter will not be included
    * in the returned {@link FastqDataStore}. All of those records 
    * must have their qualities encoded a manner 
    * that can be parsed by the given {@link FastqQualityCodec}.
    * <strong>NOTE: </strong>After parsing, the given stream will still be open!
    * It is up to the client to close the stream.  All records in the file
    * must have their qualities encoded a manner 
    * that can be parsed by the given {@link FastqQualityCodec}.
    * @param fastqStream an {@link InputStream} instance containing
    * the fastq data to parse.  This stream will not be automatically
    * closed; client code must close their stream on their own.
    * @param qualityCodec the {@link FastqQualityCodec} needed
    * to parse the encoded quality values in each record;
    * can not be null.
    * @param filter an instance of {@link FastXFilter} that can
    * be used to filter out some {@link FastqRecord}s from
    * the datastore.
    * @return a new {@link FastqDataStore} instance.
    * @throws IOException if there is a problem parsing the fastq stream.
    * @throws NullPointerException if either fastqStream or qualityCodec is null. 
    */
   public static FastqDataStore create(InputStream fastqStream, FastqQualityCodec qualityCodec,FastXFilter filter) throws IOException{
	   FastqFileDataStoreBuilderVisitor builderVisitor = createBuilder(qualityCodec, filter);
	   FastqFileParser.parse(fastqStream, builderVisitor);
	   return builderVisitor.build();
   }
    
    private static final class DefaultFastqFileDataStoreBuilderVisitor implements FastqFileDataStoreBuilderVisitor{

    	private final FastXFilter filter;
    	private String currentId;
    	private String currentComment;
    	private NucleotideSequence currentNucleotideSequence;
    	private QualitySequence currentQualitySequence;
    	private final FastqQualityCodec qualityCodec;
    	private final DefaultFastqDataStore.Builder builder;
    	
		private DefaultFastqFileDataStoreBuilderVisitor(FastqQualityCodec qualityCodec, FastXFilter filter) {
			if(qualityCodec==null){
				throw new NullPointerException("quality codec can not be null");
			}
			if(filter==null){
				throw new NullPointerException("filter codec can not be null");
			}
			this.filter = filter;
			this.qualityCodec = qualityCodec;
			builder = new DefaultFastqDataStore.Builder();
		}

		@Override
		public DeflineReturnCode visitDefline(String id, String optionalComment) {

			if(filter.accept(id, optionalComment)){
				currentId=id;
				currentComment = optionalComment;
				return DeflineReturnCode.VISIT_CURRENT_RECORD;
			}
			return DeflineReturnCode.SKIP_CURRENT_RECORD;
		}

		@Override
		public EndOfBodyReturnCode visitEndOfBody() {
			builder.put(new DefaultFastqRecord(currentId, currentNucleotideSequence, currentQualitySequence, currentComment));
			currentId =null;
			currentNucleotideSequence =null;
			currentQualitySequence=null;
			currentComment=null;
			return EndOfBodyReturnCode.KEEP_PARSING;
		}

		@Override
		public void visitLine(String line) {
			//no-op
			
		}

		@Override
		public void visitFile() {
			//no-op
			
		}

		@Override
		public void visitEndOfFile() {
			//no-op
			
		}

		@Override
		public FastqDataStore build() {
			return builder.build();
		}

		@Override
		public void visitNucleotides(NucleotideSequence nucleotides) {
			currentNucleotideSequence = nucleotides;
			
		}

		@Override
		public void visitEncodedQualities(String encodedQualities) {
			currentQualitySequence = qualityCodec.decode(encodedQualities);
			
		}
    	
    }
}
