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

package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.AbstractAceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigBuilder;
import org.jcvi.common.core.assembly.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.ace.AceContigDataStoreBuilder;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceFileWriter;
import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.ace.DefaultAceContig;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.assembly.ace.consed.PhdDirQualityDataStore;
import org.jcvi.common.core.assembly.util.slice.CompactedSliceMap;
import org.jcvi.common.core.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.common.core.assembly.util.slice.Slice;
import org.jcvi.common.core.assembly.util.slice.SliceMap;
import org.jcvi.common.core.assembly.util.slice.consensus.ConicConsensusCaller;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusResult;
import org.jcvi.common.core.assembly.util.slice.consensus.NoAmbiguityConsensusCaller;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MultipleDataStoreWrapper;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.DefaultNucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.read.trace.TraceQualityDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.MultipleWrapper;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public class RecallAceConsensus {

    private static enum RecallType{
        CONIC("conic"),
        NO_AMBIGUITY("no_ambiguity")
        
        ;
        
        private static Map<String, RecallType> map;
        static{
            RecallType[] values = values();
            map = new HashMap<String, RecallType>(values.length);
            for(RecallType value : values){
                map.put(value.type, value);
            }
        }
        private final String type;
        
        RecallType(String type){
            this.type = type;
        }
        
        public static RecallType parse(String s){
            for(Entry<String, RecallType> entry : map.entrySet()){
                String key = entry.getKey();
                if(key.equalsIgnoreCase(s)){
                    return entry.getValue();
                }
            }
            throw new IllegalArgumentException("could not parse value "+s);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String toString() {
            return type;
        }
        
    }
    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("ace","input ace file")
                            .isRequired(true)        
                            .build());
        
        options.addOption(new CommandLineOptionBuilder("o","output ace file")
        .longName("out")
        .isRequired(true)        
        .build());
        options.addOption(new CommandLineOptionBuilder("fasta","output fasta file")       
        .build());
        options.addOption(new CommandLineOptionBuilder("recall_with","consensus recall method must be either : "+ RecallType.map.keySet())
        .isRequired(true)        
        .build());
        options.addOption(CommandLineUtils.createHelpOption());
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }

        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
        
            File inputAceFile = new File(commandLine.getOptionValue("ace"));
            File consedDir = inputAceFile.getParentFile().getParentFile();
            File phdDir = ConsedUtil.getPhdDirFor(consedDir);
            File phdballDir = ConsedUtil.getPhdBallDirFor(consedDir);
            PrintWriter fastaOut = commandLine.hasOption("fasta") ?
                    new PrintWriter(new File(commandLine.getOptionValue("fasta"))):
                        null;
            
            File outputAceFile = new File(commandLine.getOptionValue("out"));
            final OutputStream out = new FileOutputStream(outputAceFile);
            PhdDataStore phdballDataStore = new PhdDirQualityDataStore(phdballDir);
            PhdDataStore phdDataStore = new PhdDirQualityDataStore(phdDir);
            PhdDataStore masterPhdDataStore= MultipleDataStoreWrapper.createMultipleDataStoreWrapper(PhdDataStore.class, phdDataStore,phdballDataStore);
            QualityDataStore qualityDataStore = TraceQualityDataStoreAdapter.adapt(masterPhdDataStore); 
            ConsensusCaller consensusCaller = createConsensusCaller(RecallType.parse(commandLine.getOptionValue("recall_with")), PhredQuality.valueOf(30));
            AceContigDataStoreBuilder aceContigDataStoreBuilder = IndexedAceFileDataStore.createBuilder(inputAceFile);
            AceFileVisitor headerVisitor = new AbstractAceFileVisitor() {
                
                /**
                * {@inheritDoc}
                */
                @Override
                public synchronized void visitHeader(int numberOfContigs,
                        int totalNumberOfReads) {
                    try {
                        AceFileWriter.writeAceFileHeader(numberOfContigs, totalNumberOfReads, out);
                    } catch (IOException e) {
                        throw new IllegalStateException("error writing to outputfile");
                    }
                }

                @Override
                protected void visitNewContig(String contigId, NucleotideSequence consensus, int numberOfBases, int numberOfReads, boolean complemented) {
                    // no-op
                    
                }
                
                @Override
                protected void visitAceRead(String readId, NucleotideSequence validBasecalls,
                        int offset, Direction dir, Range validRange,
                        PhdInfo phdInfo, int ungappedFullLength) {
                    // no-op
                    
                }
            };
            AceFileVisitor aceVisitors = MultipleWrapper.createMultipleWrapper(AceFileVisitor.class, headerVisitor,aceContigDataStoreBuilder);
            AceFileParser.parse(inputAceFile, aceVisitors);
            AceContigDataStore aceContigDataStore = aceContigDataStoreBuilder.build();
            CloseableIterator<AceContig> iter = aceContigDataStore.iterator();
            try{
	            while(iter.hasNext()){
	            	AceContig contig = iter.next();
	                System.out.println(contig.getId());
	                SliceMap sliceMap = CompactedSliceMap.create(contig, qualityDataStore, 
	                        GapQualityValueStrategies.LOWEST_FLANKING);
	                NucleotideSequence originalConsensus = contig.getConsensus();
	                NucleotideSequenceBuilder recalledConsensusBuilder = new NucleotideSequenceBuilder((int)originalConsensus.getLength());
	                for(int i=0; i<originalConsensus.getLength();i++){
	                    Slice slice =sliceMap.getSlice(i);
	                    ConsensusResult result =consensusCaller.callConsensus(slice);
	                  
	                    recalledConsensusBuilder.append(result.getConsensus());
	                }
	                final NucleotideSequence gappedRecalledConsensus = recalledConsensusBuilder.build();
	                if(fastaOut !=null){
	                    fastaOut.print(new DefaultNucleotideSequenceFastaRecord(contig.getId(), gappedRecalledConsensus.asUngappedList()));
	                }
	                AceContigBuilder builder = DefaultAceContig.createBuilder(contig.getId(), gappedRecalledConsensus);
	                CloseableIterator<AcePlacedRead> readIter = null;
	                try{
	                	readIter = contig.getReadIterator();
	                	while(readIter.hasNext()){
	                		AcePlacedRead read = readIter.next();
	                		builder.addRead(read);
	                	}
	                }finally{
	                	IOUtil.closeAndIgnoreErrors(readIter);
	                }
	                AceFileWriter.writeAceContig(builder.build(), masterPhdDataStore, out);
	            }
            }finally{
            	IOUtil.closeAndIgnoreErrors(iter);
            }
            IOUtil.closeAndIgnoreErrors(aceContigDataStore,fastaOut,masterPhdDataStore,out);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       

    }

    private static ConsensusCaller createConsensusCaller(RecallType recallType, PhredQuality highQualityThreshold){
        switch(recallType){
            case CONIC : return new ConicConsensusCaller(highQualityThreshold);
            case NO_AMBIGUITY : return new NoAmbiguityConsensusCaller(highQualityThreshold);
            default : throw new IllegalArgumentException("could not create consensus caller for type "+ recallType);
        }
    }
    /**
     * @param options
     */
    private static void printHelp(Options options) {
        // TODO Auto-generated method stub
        
    }

}
