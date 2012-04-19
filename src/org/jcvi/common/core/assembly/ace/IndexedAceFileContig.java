package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore.ReadVisitorBuilder;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;

final class IndexedAceFileContig implements AceContig{

	private final String contigId;
	private Map<String, AlignedReadInfo> readInfoMap;
	private IndexedFileRange readOffsetRanges;
	private boolean isComplimented;
	private final NucleotideSequence consensus;
	private final File aceFile;
	private final long contigStartFileOffset;
	
	
	private IndexedAceFileContig(String contigId,
			Map<String, AlignedReadInfo> readInfoMap,
			IndexedFileRange readOffsetRanges, boolean isComplimented,
			NucleotideSequence consensus, File aceFile,
			long contigStartFileOffset) {
		this.contigId = contigId;
		this.readInfoMap = readInfoMap;
		this.readOffsetRanges = readOffsetRanges;
		this.isComplimented = isComplimented;
		this.consensus = consensus;
		this.aceFile = aceFile;
		this.contigStartFileOffset = contigStartFileOffset;
	}

	@Override
	public String getId() {
		return contigId;
	}

	@Override
	public int getNumberOfReads() {
		return readInfoMap.size();
	}

	@Override
	public CloseableIterator<AcePlacedRead> getReadIterator() {
		InputStream in = null;
		try{
			in = new FileInputStream(aceFile);
			//seek to start of contig
			IOUtil.blockingSkip(in, contigStartFileOffset);
			//start parsing
			IndexedReadIterator iter = new IndexedReadIterator(in, consensus, readInfoMap);
			iter.start();
			return iter;
		} catch (IOException e) {
			throw new IllegalStateException("error iterating over reads",e);
		}
	}

	@Override
	public NucleotideSequence getConsensus() {
		return consensus;
	}

	@Override
	public AcePlacedRead getRead(String id) {
		if(!containsRead(id)){
			return null;
		}
		InputStream in = null;
		try{
			Range offsetRange = readOffsetRanges.getRangeFor(id);
			in = IOUtil.createInputStreamFromFile(aceFile, offsetRange);
			ReadVisitorBuilder builder = new ReadVisitorBuilder(consensus);
			builder.visitBeginContig(contigId, 0, 0, 0, isComplimented);
			AlignedReadInfo alignmentInfo = readInfoMap.get(id);
			builder.visitAssembledFromLine(id, alignmentInfo.getDirection(), alignmentInfo.getStartOffset());
			
			AceFileParser.parse(in, builder);
			return builder.build();
			
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("ace file no longer exists", e);
		} catch (IOException e) {
			
			throw new IllegalStateException("error parsing ace file", e);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}

	@Override
	public boolean containsRead(String readId) {
		return readInfoMap.containsKey(readId);
	}

	@Override
	public boolean isComplemented() {
		return isComplimented;
	}
	
	static final class IndexedContigVisitorBuilder implements AceFileVisitor, Builder<AceContig>{
		
		private long startOffset=0;
		private IndexedFileRange readRanges;
		private final File aceFile;
		private String currentLine;
		private String contigId;
		private boolean isComplimented;
		private Map<String, AlignedReadInfo> readInfoMap;
		private NucleotideSequenceBuilder consensusBuilder;
		private boolean readingConsensus=true;
		private int currentReadLength=0;
		private String currentReadId;
		private long contigStartOffset=0;
		
		public IndexedContigVisitorBuilder(long startOffset, File aceFile) {
			this.startOffset = startOffset;
			this.aceFile = aceFile;
			this.contigStartOffset = startOffset;
		}

		@Override
		public void visitLine(String line) {
			currentLine = line;
			if(readingConsensus){
				startOffset+=currentLine.length();
			}else{
				currentReadLength+=currentLine.length();
			}
		}

		@Override
		public void visitFile() {
			
		}

		@Override
		public void visitEndOfFile() {
			
		}

		@Override
		public AceContig build() {
			return new IndexedAceFileContig(contigId, readInfoMap, readRanges, isComplimented, 
					consensusBuilder.build(), aceFile, 
					contigStartOffset);
		}

		@Override
		public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
			
		}

		@Override
		public boolean shouldVisitContig(String contigId,
				int numberOfBases, int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplimented) {
			this.contigId =contigId;
			readRanges = new DefaultIndexedFileRange(numberOfReads);
			isComplimented = reverseComplimented;
			readInfoMap = new LinkedHashMap<String, AlignedReadInfo>(numberOfReads+1, 1F);
			consensusBuilder = new NucleotideSequenceBuilder();
			return true;
		}

		@Override
		public void visitBeginContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			
		}

		@Override
		public void visitConsensusQualities() {
			
		}

		@Override
		public void visitAssembledFromLine(String readId, Direction dir,
				int gappedStartOffset) {
			readInfoMap.put(readId, new AlignedReadInfo(gappedStartOffset, dir));
			
		}

		@Override
		public void visitBaseSegment(Range gappedConsensusRange,
				String readId) {
			
		}

		@Override
		public void visitReadHeader(String readId, int gappedLength) {
			if(readingConsensus){
				readingConsensus=false;
				startOffset -=currentLine.length();
			}
			currentReadLength = currentLine.length();
			currentReadId= readId;
		}

		@Override
		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			
		}

		@Override
		public void visitTraceDescriptionLine(String traceName,
				String phdName, Date date) {
			//end of current read
			readRanges.put(currentReadId, Range.createOfLength(startOffset, currentReadLength));
			startOffset += currentReadLength+1;
		}

		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			if(readingConsensus){
				consensusBuilder.append(mixedCaseBasecalls.trim());
			}
			
		}

		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			
		}

		@Override
		public boolean visitEndOfContig() {
			return false;
		}

		@Override
		public void visitBeginConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			
		}

		@Override
		public void visitConsensusTagComment(String comment) {
			
		}

		@Override
		public void visitConsensusTagData(String data) {
			
		}

		@Override
		public void visitEndConsensusTag() {
			
		}

		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			
		}
		
	}
	
	 private static final class IndexedReadIterator extends AbstractBlockingCloseableIterator<AcePlacedRead>{

	    	private final NucleotideSequence consensus;
	    	private final Map<String, AlignedReadInfo> readInfoMap;
	    	private final InputStream in;
	    	
			public IndexedReadIterator(InputStream in, NucleotideSequence consensus, final Map<String, AlignedReadInfo> readInfoMap) {
				this.consensus = consensus;
				this.readInfoMap = readInfoMap;
				this.in = in;
			}

			@Override
			protected void backgroundThreadRunMethod()
					throws RuntimeException {
				AbstractAceFileVisitor visitor = new AbstractAceFileVisitor() {
					{
						this.setAlignedInfoMap(readInfoMap);
					}
					@Override
					protected void visitNewContig(String contigId,
							NucleotideSequence consensus, int numberOfBases, int numberOfReads,
							boolean isComplimented) {
						//no-op
						
					}
					
					@Override
					public boolean visitEndOfContig() {
						return false;
					}

					@Override
					protected void visitAceRead(String readId,
							NucleotideSequence validBasecalls, int offset, Direction dir,
							Range validRange, PhdInfo phdInfo, int ungappedFullLength) {
						AcePlacedRead read =DefaultAcePlacedRead.createBuilder(IndexedReadIterator.this.consensus, readId, validBasecalls, offset, dir, validRange, phdInfo, ungappedFullLength)
								.build();
						
						blockingPut(read);
					}
				};
				try{
					AceFileParser.parse(in, visitor);
				} catch (IOException e) {
					throw new IllegalStateException("error parsing reads from contig in ace file",e);
				}finally{
					IOUtil.closeAndIgnoreErrors(in);
				}
				
			}
			
		}
}