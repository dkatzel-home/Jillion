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

package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;

/**
 * @author dkatzel
 *
 *
 */
public class RemoveRedundantMatePairs {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("mate1", "path to mate 1 file")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("mate2", "path to mate 1 file")
            .isRequired(true)
            .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to output dir")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("prefix", "output prefix to name new files.  New files will look like <prefix>_1.fastq and <prefix>_2.fastq")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("n", "number of bases to compare for redundancy")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("sanger", 
                "input fastq file is encoded as a SANGER fastq file (default is ILLUMINA 1.3+)")
                    .isFlag(true)
                       .build());
        
        
        options.addOption(CommandLineUtils.createHelpOption());
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            
            
            FastQQualityCodec qualityCodec =commandLine.hasOption("sanger")?
                                                FastQQualityCodec.SANGER:
                                                FastQQualityCodec.ILLUMINA;
            
            File mate1 = new File(commandLine.getOptionValue("mate1"));
            File mate2 = new File(commandLine.getOptionValue("mate2"));
            
            Range comparisonRange=Range.buildRangeOfLength(Integer.parseInt(commandLine.getOptionValue("n")));
            
            LargeFastQFileIterator mate1Iterator = LargeFastQFileIterator.createNewIteratorFor(mate1, qualityCodec);
            LargeFastQFileIterator mate2Iterator = LargeFastQFileIterator.createNewIteratorFor(mate2, qualityCodec);
            File outputDir = new File(commandLine.getOptionValue("o"));
            outputDir.mkdirs();
            String prefix = commandLine.getOptionValue("prefix");
            
            File nonRedundantMate1 = new File(outputDir,prefix+"_1.fastq");
            File nonRedundantMate2 = new File(outputDir,prefix+"_2.fastq");
            
            FileOutputStream out1 = new FileOutputStream(nonRedundantMate1);
            FileOutputStream out2 = new FileOutputStream(nonRedundantMate2);
            long recordsSeen=0;
            Set<NucleotideSequence> nonRedundantList = new HashSet<NucleotideSequence>(30000000);
            while(mate1Iterator.hasNext()){
                FastQRecord forward = mate1Iterator.next();
                FastQRecord reverse = mate2Iterator.next();
                List<Nucleotide> combinedList = new ArrayList<Nucleotide>(60);
                combinedList.addAll(forward.getSequence().asList(comparisonRange));
                combinedList.addAll(reverse.getSequence().asList(comparisonRange));
                NucleotideSequence seq =NucleotideSequenceFactory.createACGTN(combinedList);
                if(!nonRedundantList.contains(seq)){
                    out1.write(FastQUtil.encode(forward, qualityCodec).getBytes());
                    out2.write(FastQUtil.encode(reverse, qualityCodec).getBytes());
                    nonRedundantList.add(seq);
                }
                recordsSeen++;
                if(recordsSeen%100000==0){
                    System.out.println("records seen = "+recordsSeen);
                }
            }
            System.out.println("final number of mates seen ="+ recordsSeen);
            System.out.println("num mates written ="+ nonRedundantList.size());
            out1.close();
            out2.close();
            nonRedundantList.clear();
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
        
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "remove redundant mates [OPTIONS] -mate1 <fastq> -mate2 <fastq> -o <output dir> -prefix <prefix>", 
                
                "scan large fastq mate files and remove any redundant pairs",
                options,
               "Created by Danny Katzel"
                  );
    }
}
