package org.jcvi.common.core.seq.read.trace.sanger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.AcceptingFastXFilter;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;

public final class DefaultPositionFastaFileDataStore {
	
	private DefaultPositionFastaFileDataStore(){
		//can not instantiate
	}
	public static PositionSequenceFastaDataStore create(File positionFastaFile, FastXFilter filter) throws FileNotFoundException{
		PositionFastaFileVisitor builder = new PositionFastaFileVisitor(filter);
		FastaFileParser.parse(positionFastaFile, builder);
		return MapDataStoreAdapter.adapt(PositionSequenceFastaDataStore.class, builder.fastas);
	}
	public static PositionSequenceFastaDataStore create(InputStream positionFastaInputStream, FastXFilter filter) throws FileNotFoundException{
		PositionFastaFileVisitor builder = new PositionFastaFileVisitor(filter);
		FastaFileParser.parse(positionFastaInputStream, builder);
		return MapDataStoreAdapter.adapt(PositionSequenceFastaDataStore.class, builder.fastas);
	}
	public static PositionSequenceFastaDataStore create(File positionFastaFile) throws FileNotFoundException{
		return create(positionFastaFile, AcceptingFastXFilter.INSTANCE);
	}
	public static PositionSequenceFastaDataStore create(InputStream positionFastaInputStream) throws FileNotFoundException{
		return create(positionFastaInputStream, AcceptingFastXFilter.INSTANCE);
	}
	

	private static class PositionFastaFileVisitor implements FastaFileVisitor{
		/**
		 * Default capacity for position builder {@value}
		 * should be large enough to handle
		 * most sanger reads, and the builder
		 * will grow to accommodate larger reads.
		 */
		private static final int DEFAULT_INITIAL_CAPACITY = 900;
		private String currentId;
		private String currentComment;
		private PositionSequenceBuilder currentPositionBuilder;
		
		private final FastXFilter filter;
		
		private final Map<String, PositionSequenceFastaRecord> fastas= new LinkedHashMap<String, PositionSequenceFastaRecord>();
		
		public PositionFastaFileVisitor(FastXFilter filter) {
			this.filter = filter;
		}
		
		@Override
		public DeflineReturnCode visitDefline(String id, String optionalComment) {
			if(filter.accept(id, optionalComment)){
				currentId = id;
				currentComment = optionalComment;
				currentPositionBuilder = new PositionSequenceBuilder(DEFAULT_INITIAL_CAPACITY);
				return DeflineReturnCode.VISIT_CURRENT_RECORD;
			}
			return DeflineReturnCode.SKIP_CURRENT_RECORD;
		}

		@Override
		public EndOfBodyReturnCode visitEndOfBody() {
			fastas.put(currentId, new PositionSequenceFastaRecord(currentId, currentComment, currentPositionBuilder.build()));
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
		public void visitBodyLine(String bodyLine) {
			Scanner scanner = new Scanner(bodyLine);
	        while(scanner.hasNextShort()){
	            currentPositionBuilder.append(scanner.nextShort());
	        }
			scanner.close();
		}
		
	}
}
