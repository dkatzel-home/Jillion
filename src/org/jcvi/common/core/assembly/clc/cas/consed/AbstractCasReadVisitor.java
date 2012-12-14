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

package org.jcvi.common.core.assembly.clc.cas.consed;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.assembly.clc.cas.AbstractOnePassCasFileVisitor;
import org.jcvi.common.core.assembly.clc.cas.CasFileInfo;
import org.jcvi.common.core.assembly.clc.cas.CasInfo;
import org.jcvi.common.core.assembly.clc.cas.CasMatch;
import org.jcvi.common.core.assembly.clc.cas.CasTrimMap;
import org.jcvi.common.core.assembly.clc.cas.CasUtil;
import org.jcvi.common.core.assembly.clc.cas.ReadFileType;
import org.jcvi.common.core.assembly.clc.cas.TraceDetails;
import org.jcvi.common.core.assembly.clc.cas.align.CasScoringScheme;
import org.jcvi.common.core.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.common.core.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.trace.sanger.phd.Phd;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;


/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCasReadVisitor extends AbstractOnePassCasFileVisitor {

    private final File workingDir;
    private final CasTrimMap trimMap;
    private StreamingIterator<PhdReadRecord> readIterator;
    private final List<NucleotideSequence> orderedGappedReferences;
    private final TrimPointsDataStore validRangeDataStore;
    private final List<StreamingIterator<PhdReadRecord>> iterators = new ArrayList<StreamingIterator<PhdReadRecord>>();
    private final TraceDetails traceDetails;
    private AbstractCasReadVisitor(File workingDir, CasTrimMap trimMap,
            List<NucleotideSequence> orderedGappedReferences,
            TrimPointsDataStore validRangeDataStore,
            TraceDetails traceDetails) {
        this.traceDetails = traceDetails;
        this.workingDir = workingDir;
        this.trimMap = trimMap;
        this.orderedGappedReferences = orderedGappedReferences;
        this.validRangeDataStore = validRangeDataStore;
    }
    public AbstractCasReadVisitor(CasInfo casInfo) {
        this(casInfo.getCasWorkingDirectory(),
                casInfo.getCasTrimMap(),
                casInfo.getOrderedGappedReferenceList(),
                casInfo.getTrimDataStore(),
                casInfo.getTraceDetails());
    }
    protected final NucleotideSequence getGappedReference(int index){
        return orderedGappedReferences.get(index);
    }
    
    /**
     * @return the validRangeDataStore
     */
    public TrimPointsDataStore getValidRangeDataStore() {
        return validRangeDataStore;
    }
    public abstract StreamingIterator<PhdReadRecord>  createFastqIterator(File illuminaFile, TraceDetails traceDetails) throws DataStoreException;
    
    public abstract StreamingIterator<PhdReadRecord>  createSffIterator(File sffFile, TraceDetails traceDetails) throws DataStoreException;
    
    public abstract StreamingIterator<PhdReadRecord>  createFastaIterator(File fastaFile, TraceDetails traceDetails) throws DataStoreException;
    
    public abstract StreamingIterator<PhdReadRecord>  createChromatogramIterator(File chromatogramFile, TraceDetails traceDetails) throws DataStoreException;
    
    @Override
    public final synchronized void visitReadFileInfo(CasFileInfo readFileInfo) {
        super.visitReadFileInfo(readFileInfo);
        handleReadFileInfo(readFileInfo);
        for(String filename :readFileInfo.getFileNames()){
            iterators.add(createIteratorFor(filename));           
        }
        
    }
    protected void handleReadFileInfo(CasFileInfo readFileInfo){
    	//no-op
    }
    private StreamingIterator<PhdReadRecord> createIteratorFor(String filename){
    	 File file;
         try {
             file = getTrimmedFileFor(filename);
         } catch (FileNotFoundException e) {
             throw new IllegalStateException(e);
         }
         ReadFileType readType = ReadFileType.getTypeFromFile(filename);
         
         try{
            switch(readType){
	            case FASTQ: 
	            	return createFastqIterator(file, traceDetails);
	            case SFF:
	            	return createSffIterator(file, traceDetails);
	            case FASTA:
	            	if(!traceDetails.hasChromatDir()){
                        return createFastaIterator(file, traceDetails);
                    }
	            	return createChromatogramIterator(file, traceDetails);
	            default: 
	            	throw new IllegalArgumentException("unsupported type "+ file.getName());
	            }
         }catch(Exception e){
         	//close any blocking iterators
         	for(StreamingIterator<PhdReadRecord> iter : iterators){
         		IOUtil.closeAndIgnoreErrors(iter);
         	}
         	throw new RuntimeException(e);
         }
    }
      @Override
    public final synchronized void visitScoringScheme(CasScoringScheme scheme) {
        super.visitScoringScheme(scheme);
        readIterator = IteratorUtil.createChainedStreamingIterator(iterators);
    }

    
    private File getTrimmedFileFor(String pathToDataStore) throws FileNotFoundException {
            final File dataStoreFile = CasUtil.getFileFor(workingDir, pathToDataStore);
            return trimMap.getUntrimmedFileFor(dataStoreFile);
    }
    

    @Override
    protected final synchronized void visitMatch(CasMatch match, long readCounter) {
    	if(!readIterator.hasNext()){
    		//we probably don't need to close
    		//but just to be sure we don't get thread deadlock
    		 IOUtil.closeAndIgnoreErrors(readIterator);
    		 //this will happen if we run out of reads unexpectedly
    		 //wrap with more helpful error message
             throw new IllegalStateException("no more reads in input file(s) even though .cas file thinks there are");
    	}
    	PhdReadRecord readRecord =readIterator.next();
        try {
        	
            if(match.matchReported()){
            	Phd phd = readRecord.getPhd();
                String recordId = phd.getId();
                int casReferenceId = (int)match.getChosenAlignment().contigSequenceId();
                NucleotideSequence gappedReference =orderedGappedReferences.get(casReferenceId);
                CasPlacedRead placedRead = CasUtil.createCasPlacedRead(match, recordId, 
                        phd.getNucleotideSequence(), 
                        validRangeDataStore.get(recordId), gappedReference);
                visitMatch(match, readRecord, placedRead);              
            }else{
                visitUnMatched(readRecord);
            }
        } catch (Exception e) {
            IOUtil.closeAndIgnoreErrors(readIterator);
            throw new IllegalStateException("error getting parsing data for " + readRecord, e);
        }
    }
    protected abstract void visitUnMatched(PhdReadRecord readRecord) throws Exception;
    protected abstract void visitMatch(CasMatch match, PhdReadRecord readRecord, CasPlacedRead placedRead)
            throws Exception;
    

    
}