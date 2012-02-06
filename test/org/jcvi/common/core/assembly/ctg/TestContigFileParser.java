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
 * Created on Jan 16, 2009
 *
 * @author "dkatzel" +
 */
package org.jcvi.common.core.assembly.ctg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyTestUtil;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultPlacedRead;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class TestContigFileParser {
    private String pathToFile = "files/gcv_23918.contig";
    int contig_id=925;
    int contig_length = 21249;
    int numberOfReads= 210;
    ResourceFileServer RESOURCES = new ResourceFileServer(TestContigFileParser.class);

                        NucleotideSequence contigConsensus = new NucleotideSequenceBuilder(
        "TAAAGTGGCCACTAAATATGTTAAGAAGGTTACTGGCAAACTAGCCGTGCGCTTTAAGGC" +
        "GTTAGGTGTAGTCGTTGTCAGGAAAATTACTGAATGGTTTGATTTAGCCGTGGACATTGC" +
        "TGCTAGTGCCGCTGGATGGCTTTGCTACCAGCTGGTAAATGGCTTATTCGCAGTGGCCAA" +
        "TGGTGTCATAACATTTGTTCAGGAGGCACCTGAGCTAGTCAAGAATTTTGTTGCCAAGTT" +
        "CAGGGCATTTTTCAAGGTTTTGATCGACTCTATGTCGGTTTCTATCTTGTCTGGACTCAC" +
        "TGTTGTCAAGACTGCCTCAAATAGGGTATGTCTGGCTGGCAGTAAGGTTTATGAAGTTGT" +
        "GCAGAAATCTTTGTCTGCATATGTTTTGCCTGTTGGTTGCAGTGAGGCCACTTGTTTAGT" +
        "GGGCGAAAGTGAACCTGCAGTTTTTGAGGATGATGTTGTTGATGTGGTTAAGTCCCCGTT" +
        "AACATATCAAGGGTGTTGTAAACCACCCACTTCTTTCGAGAAGATTTGTATTGTGGATAA" +
        "ATTATATATGGCCAAGTGTGGTGATCAATTCTACCCCGTGGTTGTTGATAACGACACTGT" +
        "CGGCGTGTTAGACCAGTGCTGGAGGTTCCCATGTGCGGGCAAGAAAGTCGTGTTTAACGA" +
        "CAAGCCTAAAGTCAAGGAGATACCCTCCACGCGAAAGATTAAGATTATCTTCGCTCTGGA" +
        "TGCGACC-TTTGATAGTGTCCTCTCGAAGGCGTGTTCAGAGTTTGAAGTTGATAAAGATG" +
        "TTGCATTGGATGAGCTGCTTGATGTTGTGCTCGATGCAGTTGATAGTACGCTCAGCCCTT" +
        "GTAAGGAGCATGATGTGATAGGCACAAAAGTTTGTGC-TTTACTTGATAGGTTGGCAGAA" +
        "GATTATGTCTATCTTTTTGATGAAGGAGGTGATGCAGTGATTGCTCCGAGAATGTATTGT" +
        "TCTTTTTCTGCTCCTGATGATGAAGACTGCGTTGCAGCAGATGTTGTAGATGCAGATGAA" +
        "AACCAAGATGATGATGCTGACTACTCTGTAGCCCTTGTCGCCGATATCCAAGAAGAGGAC" +
        "GGCGTTGCCAAGGAGCAGGTTGAGGTGGATATGGAAATTTGCATTGCGCATACTGGTAGT" +
        "CAAGAAGAATTGACTGAGCCTGATGCTGTCGGATCTCAAACTCCCATCGCCTCTGCTGAG" +
        "GAAACCGAAGTCGGAGAGGCAAGCGACAGGGAAGGGATTGCTGAGGTGAAGGCAACTGTG" +
        "TGTGCTGCTGCTTTAGATGCCTGCCCCGATCAAGTGGAGGCATTTG-AAATTGAAAAGGT" +
        "TGAAGACTCCATCTTGAATGAGCTTCAAACCGAACTTAATGCGCCAGCGGACGAGACCAT" +
        "TGATGATGTCTACTCAGAGGCATCGTCTGCATTCTATGCTGTACCGAGTGATGAGACGCA" +
        "CTTTAAAGTGTGTGGCTTCTATTCGCCAGCTATAGAGCGTACTAATTGTTGGCTGCGTTC" +
        "TACTTTGATAGTAATGCAGAG-TTTACCTTTGGAATTTAAAGACTTAGAGATGCAAAAGC" +
        "TCTGG-TTGTCTTACAAGGCC-GGCTATGACCAACGATTTGTGGACAAACTTGTTAAGAG" +
        "TGTGCCCAAGTCGATCATTCTTCCACAAGGTGGTTATGTGGCAGATTTTGCCTATTTCTT" +
        "TTTAAGCCATTGCAGCTTTAAAGCTCATGCCAAATGGCGTTGTTTGAAGTGTGATACGGA" +
        "ATTGAAGCTGCAGGGTTTGGATGCCATGTTCTTTTATGGAGACGTTGTGTTTCATATGTG" +
        "TAAGTGTGGTAGTGGCATGACCTTGTTGTCAGCGGACATACCGTACACTTTTCATTTTGG" +
        "AGTGCGAGATGATAAATTTTGCGCTTTTCACACGCCAAGAAAGGTCTTTAGGGCTGCTTG" +
        "TGTGGTTGATGTTAATGATTGCCACTCTATGGCTGTTGTAGATGGCAAGCAAATTGATGG" +
        "TAAAGTTGTTACCAAATTTAGTGGTGACAAATTTGATTTTATGGTGGGTCATGGGATGAC" +
        "ATTTAGTATGTCACCCTTTGAGACTGCCCAGTTATATGGTTCATGTATAACACCAAATGT" +
        "TTGTTTTGTTAAAGGAGATGTCATAAAGGTTGCTCGCTTGGTTGAGGCTGAAGTCATTGT" +
        "TAACCCTGCTAATGAGCGGATGGCTCATGGTGCAGGTGTTGCAGGTGCTATAGCTAAAGC" +
        "GGCGGGCAAGTCTTTTATTAAAGAAACTGCCGATATGGTTAAGAGTCAAGGTGTTTGTCA" +
        "AGTAGGCGAATGCTATGAATCTGCCGGTGGTAAGTTATGTAAAAAGGTGCTTAACATTGT" +
        "AGGACCAGATGCTCGAGGTCAAGGCAAGCAATGCTATTCACTTTTAGAGCGTGCTTATCA" +
        "GCACCTTAATAAGTGTGACAATGTTGTTACCACTTTAATATCAGCTGGTATATTCAGTGT" +
        "GCCTACTGATGTGTCTTTGACTTATTTACTTGGTGTTGTGACAAAGAATGTTATTCTTGT" +
        "TAGCAACAACAAAGATGATTTTGATGTGATAGAGAAGTGTCAAGTGACTTCTGTCGTCGG" +
        "CACTAAAGCGCTATCGCTTCAATTGGCAAAAAATTTGTGCCGAGATATAACGTTTGAGAC" +
        "GAATGCATGTGACTTGCTTGTTAATGCATCTTGCTTCGTCGCAAGCTATGAAGTGTTGCA" +
        "GGAAGTTGAACTGCTGCGACATGATATACAA-TTGGATGCTGACGCACGTGTCTTTGTGC" +
        "AGGCTAATATGGATTGTCTGCCCGCAGACTGGCGTCTTGTTAATAAATTAGATGTTGTTG" +
        "ATGGCGTTAGAACCATTAAGTATTTTGAGTGTCCGGGAGGGA-TTTTTGTGTCTAGCCAG" +
        "GGCAAGAATTTTGGTTATGTTCAGAATGGTTCATTTAAAGTAGCGAGTGTTAGCCAAAT-" +
        "AAGGGCTCTACTTGCTAATAAGGTTGATGTCTTGTGCACTGTAGACGGTGTTAACTTCCG" +
        "CTCTTGCTGTGTAGCGGAGGGTGAACTTTTTGGCAAGACATTAGGTTCAGTTTTTTGTGA" +
        "TGGCATAAATGTCACTAAGATCAGATGTAGTGCCGTTCACAAGGGCAAGGTATTTTTTCA" +
        "GTATAGTGGATTGTCTATGACAGATCTTGTAGCTGTTAAGGATGCTTTTGGTTTTGATGA" +
        "ACCACAGCTGCTGAAGTACTACAATATGCTAGGCATGTGTAAGTGGCCAGTAGTTGTTTG" +
        "TGGCAGTTACTTTGCCTTTAAGCAGGCGAATAACAATTGCTACATAAATGCGGCTTGTTT" +
        "AATGCTGCAGCATTTGAATTTAAAGTTTCCTAAGTGGCAATGGCAGGAGGCCTGGAACGA" +
        "GTTCCGCTCCGGTAAGCCACTGAGGTTCGTGTCCTTGGTTTTAGCGAGGGGCAGCTTTAA" +
        "ATTTAATGAACCTTCTGATTCAACTGATTTTATACGTGTGGTGCTGCGCGAAGCGGA-TT" +
        "TGAGTGGTGCCACATGCGA-TTTGGAA-TTT-ATTTGTAAATGTGGTGTTAAGCAAGAGC" +
        "AGCGCAAAGGTGTTGACGCTGTTATGCATTTTGGTACKTTGGATAAAAGTGATCTTGTTA" +
        "AGGGTTATAATATCGCATGTACGTGTGGTAATAAACTTGTGCATTGCACCCAATTCAACG" +
        "TACCATTTTTAATCTGCTCCAACACGCCAGAGGGTAAGAAATTGCCTGATGACGTTGTTG" +
        "CAGCTAATATCTTTACTGGTGGTAGCTTGGGCCATTACACTCATGTGAAATGCAAACCTA" +
        "AGTACCAGCTTTATGATGCTTGCAATGTGAGTAAGGTTTCTGAGGCCAAGGGTAAATTTA" +
        "CCGATTGTCTCTACCTTAAAAATTTAAAACAAACTTTTTCGTCCAAGTTGACGACTTTTT" +
        "ATTTAGATGATGTAAAGTGTGTGGAGTGTAATCCAGAGCTGAGTCAGTATTATTGTGAGT" +
        "CTGGAAAATA-TTATACAAAACCCATTATCAAGGCCCAATTTAGAACATTTGAGAAGGTT" +
        "GATGGTGTCTATACCAACTTTAAATTGGTGGGACATAGTATAGCTGAAAAATTCAATGCT" +
        "AAGTTGGGATTTGATTGTAATTCCCCTTTTGTGGAGTTTAAAATTACAGAGTGGCCAACA" +
        "GCTACTGGAGATGTGGTGTTGGCTAGTGATGATTTGTATGTGAGTCGTTATTCAGGCGGG" +
        "TGCGTTACTTTTGGTAAGCCGGTTATCTGGCTGGGCCATGAAGAGGCATCGCTGAAATCT" +
        "CTCACATATTTTAATAGACCTAGTGTCGTTTGTGAAAATAAGTTTAATGTGTTACCTGTT" +
        "GATGTCAGTGAACCCACGGATAAGGGGCCTGTGCCTGCTGCAGTCCTTGTTACCGGTGCT" +
        "GTGAGTGGTGCAAATGCGTCCACTGAGCCCGGTACGGTCAAGGAGCAAAAGTCTTGTGCC" +
        "TCTGCTAGTGTGGCGGATCAGGTTGTTACGGAAGTCCCCCAAGAGCCATCTGTTTCAGCT" +
        "GCTGATGTCAAAGAGGTTAAATTGAATGGTGTTAAAAAGCCTGTTAAGGTGGAAGGTAGT" +
        "GTGATTGTTAATGATCCCACTAGCGACACCAAAGTTGTTAAAAGTTTGTCTATTGTTGAT" +
        "GCTTATGATATGTTCCTGACAGGGTGTAAGTATGTGGTCTGGACTGCTAATGAGCTGTCT" +
        "C-GACTAGTAAATTCACCGACTGTTAGAGAGTATGTGAAGTGGGGTATGACTAAAATTGT" +
        "AATACCCGCTACTTTATTGTTATTAAGAGATGAGAAGCAAGAGTTCGTGGCACCAAAAGT" +
        "AGTTAAGGCGAAAGCTATAGCCTGCTATGGTGCTGTGAAGTGGTTCTTCCTTTATTGTTT" +
        "TAGTTGGATAAAGTTTAATACTGATAATAAGGTTATATACACCACAGAAGTGGCTTCAAA" +
        "GCTTAATTTTAAGTTGTGTTGTTTGGCCTTTAAGAATGCTTTACAGACGTTTAATTGGAG" +
        "TGTTGTGTACAGGGGCTTCTTTCTAGTGGCAACAGTCTTTTTATTATGG-TTTAACTTTT" +
        "TGTATGCCAATGTTATTTTGAGTGACTTTTATTTGCCTAATATCGGATCTCTCCCTACTT" +
        "TTGTGGGGCAGATTGTTGCTTGGGTTAAGACCACATTT-GGCGTGTCAACCATCTGTGAT" +
        "TTTTACCATGTGACAGATGTGGGCTATAGGAGTTCGTTTTGCAATGGAAGCATGGTATGT" +
        "GAATTATGCTTCTTAGGTTTTGACATGTTGGACAACTATGATGCCATAAATGTTGTTCAA" +
        "CATGTTGTGGATAGGCGAGTTTCTTTTGATTATATCAGCCTATGTAAATTAGTGGTCGAG" +
        "CTCATTATCGGCTACTCGCTTTATACTGTGTGCTTCTACCCACTGTTTGTCCTTATTGGA" +
        "ATGCAGTTGTTGACCACATGGTTGCCTGAATTTTTTATGCTGGAGACTATGCATTGGAGC" +
        "GCCCGTTTGGTTGTGTTTGTTGCTAATATGATCCCAGCTTTTACTTTACTGCGATTTTAC" +
        "ATCGTGGTGACAGCTATGTATAATGTTTATTGTCTTTGTAGACATGTTATGTATGGATGT" +
        "AGTAAGCCTGGTTGCTTGTTTTGTTATAAGAGAAACCGTAGTGTCCGTGTTAAGTGTAGC" +
        "ACCGTAGTTTGTGGTTCACTACGCTATTACGATGTAATGGCTAACGGCGGCACAGGTTTC" +
        "TGCACAAAGCACCAGTGGAACTGTCTTAATTGCAATTCCTGGAAACCAGGCAATACATTC" +
        "ATAACCATTGAAGCAGCGGCAGACCTCTCTAGGGAGTTGAAACGTCCTGTGAATCCTACA" +
        "GACTCTGCTTATTACTCGGTCACAGAGGTTAAGCAGGTTGGTTGTTCAATGCGTTTGTTC" +
        "TATGAGAGAGATGGAAAGCGTGTTTATGATGATGTTAGTGCTAGTTTGTTTGTGGACATG" +
        "AATGGTCTGCTGCATTCTAAAGTTAAAGGTGTGCCAGAAACTCATGTTGTAGTTGTTGAG" +
        "-AACGAAGCCGATAAGGCTAGTTTTCTTAACGC-TGCTGTTTTCTATGCACAATCTCTTT" +
        "ATAGACCGATGCTGATGATGGAGAAGAAGTTAATAACCACTGCTAACACTGGTTTGTCTG" +
        "TTAGTCGAACTATGTTTGACCTCTATGTAGATTCATTGCTGAATGTCCTTGACGTGGATC" +
        "GCAAGAGTCTAACAAGTTTTGTAAATGCTGCGCACACTTCTTTAAAGGAGGGTGTGCAGC" +
        "TTGAACAGGTTATGGACACCTTTGTTGGCTGCGCTCGACGTAAGTGTGTTATAGATTCTG" +
        "ATGTTGAAACCAGGTCTATTACCAAGTCCGTTATGTCAGCAGTAAATGCGGGTGTTGATT" +
        "TTACGGATGAGAGTTGTAATAATTTGGTGCCTACCTATGTCAAAAGTGATACTATCGTTG" +
        "CCGCTGATTTGGGTGTTCTTATTCAGAATAATGCTAAGCATGTACAATCTAATGTTGCAA" +
        "AAGCCGCTAATGTGGCTTGCATCTGGTCTGTGGATGCTTTTAATCAGCTGTCTGCTGACT" +
        "TGCAGCATAGGCTTAGAAAAGCATGTTCAAAAACAGGCTTAAAGATTAAGCTTACCTATA" +
        "ATAAGCAGGAGGCAAGCGTTCCTATTTTAACCACACCGTTCTCTCTAAAAGGAGGCGCTG" +
        "TGTTTAGTAAATTTCTTCAATGGTTATTTGTTGCTAATTTGATTTGTTTCATTGTATTGT" +
        "GGGCTCTTATGCCGACTTATGCAGTGCACAAATCAGATATGCAGTTGCCTTTATATGCCA" +
        "GTTTTAAAGTTATAGAAAATGGTGTGTTAAGAGATGTGTCTGTTACTGACGCATGCTTCG" +
        "CAAACAAATTTAATCAATTTGATCAATGGTATGAGTCTACATTTGGTCTTGCCTATTACC" +
        "GTAACTCTAAGGCGTGTCCTGTTGTGGTTGCTGTTATAGACCAAGACATTGGCCATACCT" +
        "TATTTAATGTTCCTACCAAAGTTTTAAGACACGGATTTCATGTGTTGCATTTTATAACTC" +
        "ATGCATTTGCTACTGATAGCGTGCAGTGTTATACGCCACATATGCAAATTCCTTATGATA" +
        "CCTTCTATGCTAGTGGTTGCGTGTTGTCGTCTCTCTGTACTATGCTAGCGCATGCAGATG" +
        "GAACCCCGCATCCTTATTGTTATACGGAGGGTGTTATGCATAATGCTTCTCTGTATAGTT" +
        "CTTTGGTCCCTCATGTCCGTTATAACCTAGCTAGTTCAAATGGTTATATACGTTTTCCTG" +
        "AAGTTGTTAGTGAGGGCATTGTGCGTGTTGTGCGCACTCGCGCTATGACCTACTGTAGGG" +
        "TCGGTTTATGTGAGGAGGCCGAGGAGGGTATCTGTTTTAATTTTAATAGTTCATGGGTAC" +
        "TGAACAACCCGTATTATAGGGCTATGCCTGGAACGTTTTGTGGTAGGAATGTTTTTGATT" +
        "TAATACACCAAGTTATAGGAGGTTTAGTCCAGCCTATTGATTTCTTTGCTTTAACGGCGA" +
        "GTTCAGTGGCTGGTGCTATCCTTGCAATTATTGTTGTTTTAGCT-TTTTATTACTTAATA" +
        "AAGCTTAAACGTGCCTTTGGTGACTACACTAGTGTTGTAGTGATCAATGTCATTGTGTGG" +
        "TGTATAAATTTCCTGATG-CTATTTGTGTTTCAGGTTTATCCCACCTTGTCTTGTTTATA" +
        "TGCTTTTTTTTATTTTTATATGACGCTCTATTTCCCCTCGGAGATAAGTGTCGTTATGCA" +
        "TTTGCAGTGGCTTGTCATGTATGGTGCTATTATGCCCTTGTGGTTCTGCATTATTTACGT" +
        "GGCAGTCGTTGTTTCAAACCATGCTTTGTGGTTGTTCTCTTACTGCCGCAAAAT-TGGTA" +
        "CTGAGGTTCGTAGTGATGGCACATTTGAAGAAATGGCCCTTACTACCTTTATGATTACTA" +
        "AAGAATCCTATTGTAAGTTGAAAAATTCTGTTTCTGATGTTGCTTTTAACAGGTACTTGA" +
        "GTCTTTATAACAAGTATCGCTATTTTAGTGGCAAAATGGATACTGCTGCTTATAGAGAGG" +
        "CTGCCTGTTCGCAACTGGCAAAGGCTATGGAAACTTTTAACCATAACAATGGTAATGATG" +
        "TTCTCTATCAGCCTCCCACTGCCTCTGTTACTACATCATTTTTACAGTCTGGTATAGTC-" +
        "AAAATGGTGTCTCCCACATCGAAAGTGGAACCCTGTGTAGTTAGTGTTACCTATGGTAAT" +
        "ATGACACTTAATGGGTTGTGGTTGGATGATAAAGTTTATTGTCCAAGACATGTAATTTGT" +
        "TCTTCAGCTGACATGACAGACCCTGATTATCCTAATTTGCTTTGTAGAGTGACATCAAGT" +
        "GATTTTTGTGTTATGTCTGACCGTATGAGCCTTACCGTGATGTCTTACCAAATGCAGGGC" +
        "AGTCTACTTGTTTTGACTGTAACGTTGCAAAATCCTAACACACCAAAGTATTCCTTCGGT" +
        "GTTGTTAAGCCTGGTGAGACGTTTACAGTTTTGGCTGCATACAATGGCAAACCCCAAGGA" +
        "GCCTTCCATGTTGTTATGCGTAGCAGTCATACCATAAAGGGCTCCTTTTTGTGTGGATCT" +
        "TGCGGTTCTGCAGGATATGTTTTAACTGGCGATAGTGTACGATTTGTTTATATGCATCAG" +
        "CTTGAGTTGAGTACTGGTTGTCATACCGGTACTGACCTTAACGGGAACTTTTATGGTCCC" +
        "TATAGAGATGCTCAGGTTGTACAATTGCCAGTTCAAGATTATACGCAGACTGTTAATGTT" +
        "GTAGCTTGGCTTTACGCTGCTATTCTTAACAGGTGCAATTGGTTTGTGCAAAGTGATAGT" +
        "TGTTCTCTGGAAGAATTTAATGTTTGGGCTATGACCAATGGTTTTAGTTCAATCAAAGCT" +
        "GATCTTGTTTTGGATGCGCTTGCTTCTATGACAGGCGTTACAGTTGAACAGGTGTTGGCT" +
        "GCTATTAAGCGGCTTCATTCTGGATTCCAGGGCAAACAAATTTTAGGTAGTTGTGTGCTT" +
        "GAAGATGAGCTGACACCCAGCGATGTCTATCAACAACTAGCTGGTGTTAAGTTACAGTCA" +
        "AAGCGCACAAGAGTTATTAAAGGCACATGTTGCTGGATATTGGCTTCAACATTTCTGTTT" +
        "TGTAGCATTATCGCAGCATTTGTAAAATGGACTATGTTTATGTATGTTACTACCCATATG" +
        "TTGGGCGTGACATTGTGTGCACTTTGCTTTGTAAGCTTTGCTATGTTGTTGATCAAGCAT" +
        "AAGCATTTGTATTTAACTATGTATATTATGCCTGTGTTATGCACATTGTTTTACACCAAT" +
        "TATTTGGTTGTGTATAAACAGAGTTTTAGAGGCCTTGCTTATGCTTGGCTTTCACATTTT" +
        "GTCCCTGCTGTAGATTATACATATATGGATGAAGTTTTATATGGTGTTGTGTTGCTAGTC" +
        "GCTATGGTGTTTGTCACCATGCGTAGCATAAATCACGACGTATTCTCTATTATGTTCTTG" +
        "ATTGGTAGACTTGTCAGCCTGGTATCTATGTGGTATTTTGGAGCCAATTTAGAGGAAGAG" +
        "ATACTATTGTTCCTCACAGCCTTATTTGGCACGTACACATGGACCACCATGTTGTCATTG" +
        "GCTACGGCCAAGGTCATTGCTAAATGGTTGGCTGTGAATGTCTTGTACTTCACAGACGTA" +
        "CCGCAAATAAAATTAGTTCTTTTGAGCTATTTGTGTATAGGTTATGTGTGTTGTAGTTAT" +
        "TGGGGCGTCTTGTCACTCCTTAATAGCATTTTTAGGATGCCATTGGGCGTCTACAATTAT" +
        "AAAATCTCCGTGCAGGAGTTACGTTTTATGAATGCTAATGGCTTGCGCCCACCCAGAAAT" +
        "AGTTTTGAAGCCTTGGTGCTTAATTTTAAGCTGTTGGGAATTGGTGGTGTGCCAGTCATT" +
        "GAAGTATCTCAGATTCAATCAAGATTGACGGATGTTAAATGTGCTAATGTTGTGTTGCTT" +
        "AATT-GCCTCCAGCACTTGCATATTGCATCTAATTCTAAGTTGTGGCAGTATTGCAGTAC" +
        "TTTGCATAATGAAATACTGGCTACATCTGATTTGAGCGTTGCCTTCGATAAGTTGGCTCA" +
        "GCTCTTAGTTGTTTTATTTGCTAATCCAGCAGCTGTGGATAGCAAGTGTCTTGCAAGTAT" +
        "TGAAGATGTGAGCGATGATTACGTTCGCGACAATACTGTCTTGCAAGCTCTACAGAGTGA" +
        "ATTTGTTAATATGGCTAGCTTCGTTGAGTATGAACTTGCTAAGAAGAACTTAGATGAAGC" +
        "CAAGGCTAGCGGCTCTGCTAATCAACAGCAGATTAAGCTGCTAGAGAAGGCTTGTAATAT" +
        "TGCTAAGTCAGCATATGAGCGCGATAGAGCTGTTGCTCGTAAGCTGGAACGTATGGCTGA" +
        "TTTAGCTCTTACAACTATGTACAAGGAAGCTAGAATTAATGATAAGAAGAGTAAGGTTGT" +
        "GTCGGCATTGCAAACCATGCTTTTTAGTATGGTTCGTAAGCTAGATAACCAATCTCTTAA" +
        "TTCTATTTTAGATAATGCAGTTAAGGGTTGTGTACCTTTGAATGCAATACCATCATTGAC" +
        "TTCAAACACTCTGACCATAATAGTGCCAGATAAGCAGGTTTTTGATCAAGTCGTGGATAA" +
        "TGTGTATGTCACCTATGCTGGGAATGTATGGCATATACAGTCTATTCAAGATGCTGATGG" +
        "TGCTGTTAAACAATTGAATGAGATTGATGTTAATTCAACCTGGCCCCTAGTCATTGCTGC" +
        "AAATAGGCATAATGAAGTGTCTACTGTCGTTTTGCAGAACAATGAGTTGATGCCTCAGAA" +
        "GTTGAGAACTCAGGTAGTCAATAGTGGCTCAGATATGAATTGTAATACTCCTACCCAGTG" +
        "TTATTATAATACTACTGGCACGGGTAAGATTGTGTATGCTATACTTAGTGACTGTGATGG" +
        "TCTCAAGTACACTAAGATAGTAAAAGAAGATGGAAATTGTGTTGTTTTGGAATTGGATCC" +
        "TCCCTGTAAATTTTCAGTTCAGGATGTGAAGGGCCTTAAAATTAAGTACCTTTACTTTGT" +
        "TAAGGGGTGTAATACATTAGCTAGAGGATGGGTTGTAGGCACCTTATCATCGACAGTGAG" +
        "ATTGCAGGCAGGTACGGCAACTGAGTATGCCTCCAACTCTGCAATACTGTCGTTGTGTGC" +
        "GTTTTCTGTAGATCCTAAGAAAACTTACTTGGACTATATACAACAGGGTGGAGTTCCTGT" +
        "TACTAATTGTGTTAAGATGTTATGTGATCATGCTGGTACTGGTATGGCCATTACTATTAA" +
        "GCCGGAGGCAACCACTAACCAGGATTCTTATGGTGGTGCTTCTGTTTGTATATATTGCCG" +
        "CTCGCGTGTTGAACATCCAGATGTTGATGGATTGTGCAAATTACGCGGCAAGTTTGTTCA" +
        "AGTTCCCTTAGGTATAAAAGATCCTGTGTCATATGTTTTGACGCATGATGTTTGTCAGGT" +
        "TTGTGGCTTTTGGCGAGATGGTAGCTGTTCCTGTGTAGGCACAGGCTCCCAGTTTCAGTC" +
        "AAAAGACACGAACTTTTTAAACGGGTTCGGGGTACAAGTGTAAATGCCCGTCTTGTACCC" +
        "TGTGCCAGTGGCTTGGACACTGATGTTCAATTAAGGGCATTTGATATTTGTAATGCTAAT" +
        "CGAGCTGGCATTGGTTTGTATTATAAAGTGAATTGCTGCCGCTTCCAGCGAGTAGATGAG" +
        "GACGGCAACAATTTGGATAAGTTCTTTGTTGTTAAAAGAACCAATTTAGAAGTGTATAAT" +
        "AAGGAGAAAGAATGCTATGAGTTGACAAAAGAATGCGGCGTTGTGGCTGAACACGAGTTT" +
        "TTTACATTCGATGTTGAGGGAAGTCGAGTACCACACATTGTTCGCAAGGATCTTTCAAAG" +
        "TATACTATGTTGGATCTTTGCTATGCATTGCGGTATTTTGACCGCAATGATTGTTCAACT" +
        "CTTAAAGAAATTCTCCTTACATATGCTGAATGTGAAGACTCCTACTTCCAGAAGAAGGAC" +
        "TGGTATGATTTTGTTGAGAATCCTGATATTATTAATGTGTACAAGAAGCTCGGGCCTATA" +
        "TTTAATAGAGCCCTGGTTAACACTGCTAAGTTTGCAGACACATTAGTGGAGGCAGGCCTA" +
        "GTAGGTGTTTTAACACTTGATAATCAAGATTTGTATGGTCACTGGTATGACTTTGGAGAC" +
        "TTTGTCAAGACAGTGCCTGGTTGTGGTGTTGCCGTGGCARACTCTTATTATTCCTACATG" +
        "ATGCCAATGCTGACTATGTGTCATGCGTTGGATAATGAGTTGTTTGTTAATGGTATTTAT" +
        "AGGGAGTTTGACCTTGTGCAGTATGATTTTACTGATTTCAAGCAAGAGCTCTTTAATAAG" +
        "TATTTTAAGCATTGGAGTATGACCTATCATCCGAACACCTGTGAGTGCGAGGATGACAGG" +
        "TGCATTATTCATTGCGCCAATTTTAACATACTTTTCAGTATGGTTTTACCTAAGACCTGT" +
        "TTTGGGCCTCTTG-TTAGGCAAATATTTGTGGATGGTGTACCTTTCGTTGTTTCGATCGG" +
        "ATACCACTACAAAGAATTAGGTGTTGTTATGAATATGGATGTGGATACACATCGTTATCG" +
        "TCTGTCTCTTAAAGACTTGCTTTTGTATGCTGCAGATCCCGCCCTTCATGTGGCGTCTGC" +
        "TAGTGCATTGCTTGATTTGCGCACATGTTGTTTTAGCGTAGCAGCTATTACAAGTGGCGT" +
        "AAAATTTCAAACAGTTAAACCTGGCAATTTTAATCAGGATTTTTATGAGTTTATTTTGAG" +
        "TAAAGGCCTGCTTAAAGAGGGGAGCTCCGTTGATTTGAAGCACTTCTTCTTTACGCAGGA" +
        "TGGTAATGCTGCTATTACCGATTATAATTATTATAAGTATAATCTCCCCACTATGGTGGA" +
        "TATTAAACAGTTGTTGTTTGTTTTGGAAGTTGTTAATAAGTATTTTGAGATCTATGAGGG" +
        "TGGGTGTATACCCGCAACACAGGTCATTGTTAATAATTATGACAAGAGTGCTGGCTATCC" +
        "ATTTAATAAATTTGGAAAGGCCAGACTCTACTATGAAGCATTATCATTTGAGGAGCAGGA" +
        "TGAAATTTATGCGTATACTAAACGCAATGTCCTGCCAACCTTAACTCAAATGAATCTTAA" +
        "ATATGCTATTAGTGCTAAGAATAGAGCCCGCACTGTTGCTGGTGTCTCCATCCTTAGCAC" +
        "TATGACTGGCAGAATGTTTCATCAAAAGTGTTTAAAGAGTATAGCAGCTACTCGTGGTGT" +
        "GCCTGTAGTTATAGGCACCACGAAGTTTTATGGCGGTTGGGATGATATGTTACGCCGCCT" +
        "TATTAAAGATGTTGATAATCCTGTACTTATGGGTTGGGACTATCCTAAGTGTGATCGTGC" +
        "TATGCCAAACATACTAC-GTATTGTTAGTAGTTTGGTGTTAGCCCGTAAACATGATTCGT" +
        "GCTGTTCGCATACAGATAGATTCTATCGTCTTGCGAACGAGTGCGCCCAAGTTTTGAGTG" +
        "AAATTGTTATGTGTGGTGGTTGTTATTATGTTAAACCTGGTGGCACTAGCAGTGGGGATG" +
        "CAACCACTGCCTTTGCTAATTCTGTTTTTAACATTTGTCAAGCTGTCTCCGCCAATGTAT" +
        "GCTCGCTTATGGCATGCAATGGACACAAAATTGAAGATTTGAGTATACGCGAGTTGCAAA" +
        "AGCGCCTATATTCTAATGTGTATCGCGCGGACCATGTTGACGCTGCATTTGTTAGTGAGT" +
        "ATTATGAGTTTTTAAATAAGCATTTTAGTATGATGATTTTGAGTGATGATGGTGTTGTGT" +
        "GTTATAATTCAGAGTTTGCGTCCAAGGGTTATATTGCTAATATAAGTGCCTTTCAACAGG" +
        "TATTATATTATCAAAATAATGTGTTTATGTCTGAGGCCAAATGTTGGGTCGAAACAGACA" +
        "TTGAAAAGGGACCGCATGAATTTTGTTCTCAACACACAATGCTAGTAAAGATGGATGGTG" +
        "ATGAAGTTTACCTTCCATATCCTGATCCTTCGAGAATCTTAGGAGCAGGCTGTTTTG-TT" +
        "GATGATTTATTAAAGACTGATAGCGTTCTCTTGATAGAGCGCTTTGTAAGTCTTGCAATT" +
        "GATGCTTATCCGTTAGTACACCATGAGAACCCAGAGTATCAAAATGTGTTCCGGGTATAT" +
        "TTAGAATATATAAAGAAGCTGTACAACGATCTCGGTAATCAGATCCTGGACAGCTACAGT" +
        "GTTATTTTAAGTACTTGTGATGGTCAAAAGTTTACTGATGAGACCTTCTACAAGAACATG" +
        "TATTTAAGAAGTGCAGTGATGCAAAGCGTTGGTGCCTGCGTTGTCTGTAGTTCTCAAACA" +
        "TCATTACGTTGTGGCAGTTGCATACGCAAGCCATTGCTGTGCTGCAAGTGCTCATATGAT" +
        "CATGTTATGGCTACTGATCACAAATATGTCCTGAGTGTTTCACCATATGTTTGTAATTCG" +
        "CCAGGATGTGATGTAAATGATGTTACCAAATTGTATTTAGGTGGTATGTCATATTATTGT" +
        "GAGGATCATAAGCCACAGTATTCATTCAAATTGGTGATGAATGGTATGGTTTTTGGTTTA" +
        "TATAAACAATCTTGTACTGGTTCGCCGTATATAGAGGATTTTAATAAAATAGCTAGTTGC" +
        "AAATGGACAGAAGTCGATGATTATGCGCTAGCTAATGAATGTACCGAGCGCCTTAAATTG" +
        "TTTGCCGCAGAAACGCAGAAGGCCACAGAGGAGGCCTTTAAGCAATGTTATGCGTCAGCA" +
        "ACAATCCGCGAAATCGTGAGCGATCGTGAGTTAATTTTGTCTTGGGAAATTGGTAAAGTG" +
        "AGACCACCACTTAATAAAAATTATGTTTTTACTGGCTACCATTTTACTAATAATGGTAAG" +
        "ACAGTTTTAGGTGAGTATGTTTTTGATAAGAGTGAGTTGACTAATGGTGTGTATTATCGC" +
        "GCCACAACCACTTATAAGCTATCTGTTGGTGATGTTTTCATTTTAACATCACATGCAGTG" +
        "TCTAGTTTAAGTGCTCCTACATTAGTACCGCAGGAGAATTATACTAGCATTCGCTTTGCT" +
        "AGCGTTTATAGTGTGCCTGAGACGTTTCAGAACAATGTGCCTAATTATCAGCACATTGGA" +
        "ATGAAGCGATATTGTACTGTACAGGGACCGCCTGGTACTGGTAAGTCCCATCTAGCCATT" +
        "GGGCTAGCTGTTTATTATTGTACAGCGCGCGTGGTATACACCGCTGCCAGCCATGCTGCA" +
        "GTTGACGCGCTGTGTGAAAAGGCACATAAATTTTTAAATATTAATGACTGCACGCGTATT" +
        "GTTCCTGCAAAGGTGCGTGTAGATTGCTATGATAAATTTAAGGTCAATGACACTACTCGT" +
        "AAGTATGTGTTTACTACAATAAATGCATTACCTGAATTGGTGACTGACATTATTGTTGTT" +
        "GATGAAGTTAGTATGCTTACCAATTATGAGCTGTCTGTTATTAACAGTCGTGTTAGGGCT" +
        "AAGCATTATGTTTATATTGGAGATCCTGCTCAGTTACCTGCACCTCGTGTGCTGCTGAAT" +
        "AAGGGAACTCTAGAACCTAGATATTTTAATTCCGTTACCAAGCTAATGTGTTGTTTGGGT" +
        "CCAGATATTTTCTTGGGCACCTGTTATAGATGCCCTAGGGAGATTGTGGATACGGTGTCA" +
        "GCCTTGGTTTATAATAATAAGCTGAAGGCTAAAAATGATAATAGCTCCATGTGTTTTAAG" +
        "GTCTATTATAAAGGCCAGACTACACATGAGAGTTCTAGTGCTGTCAATATGCAGCAAATA" +
        "CATTTAATTAGTAAGTTTTTAAAGGCAAATCCCAGTTGGAGTAATGCCGTATTTATTAGT" +
        "CCCTATAATAGTCAGAACTATGTTGCTAAGAGGGTCTTGGGATTACAAACCCAGACAGTG" +
        "GATTCAGCGCAGGGTTCTGAATATGATTTTGTTATTTATTCGCAGACTGCGGAAACAGCG" +
        "CATTCTGTTAATGTAAATAGATTCAATGTTGCTATTACACGTGCTAAGAAGGGTATCCTC" +
        "TGTGTCATGAGTAGTATGCAATTATTTGAGTCTCTTAATTTTACTACACTGACTCTGGAT" +
        "AAGATTAACAATCCACGATTGCAGTGTACTACAAATTTGTTTAAGGATTGTAGCAAGAGC" +
        "TATGAGGGGTATCACCCAGCCCATGCACCGTCCTTTTTGGCAGTGGATGATAAATATAAG" +
        "GTAGGCGGTGATTTAGCCGTTTGCCTTAATGTTGCTGATTCTTCTGTCATTTACTCGCGG" +
        "CTTATATCACTCATGGGATTCAAGCTTGACTTGACCCTTGATGGTTATTGTAAGCTGTTT" +
        "ATAACAAGAGATGAAGCTATCAAACGTGTTAGAGCTTGGGTTGGCTTTGACGTAGAAGGC" +
        "GCTCATGCGACGCGTGATAGCATTGGGACAAACTTCCCATTACAATTAGGCTTTTCGACT" +
        "GGAATTGATTTCGTTGTCGAGGCCACTGGGATGTTTGCTGAGAGAGAAGGCTATGTCTTT" +
        "AAAAAGGCAGCAGCACGAGCTCCTCCCGGCGAACAATTTAAACACCTCGTTCCACTTATG" +
        "TCTAGAGGGCAGAAATGGGATGTGGTTCGAATTCGAATAGTACAAATGTTGTCAGACCAC" +
        "TTAGTGGGTTTGGCAGATAGTGTTGTACTTGTGACGTGGGCTGCCAGCTTTGAGCTCACA" +
        "AGTTTGCGATATTTCGCTAAAGTCGGAAAAGAAGTTGTTTGTAGTGTCTGCAATAAGCGG" +
        "GCGACATGTTTTAATTCTAGAACTGGATACTATGGATGCTGGCGACATAGTTATTCCTGT" +
        "GATTATCTGTATAACCCACTAATAGTTGATATTCAACAGTGGGGATATACAGGATCTTTA" +
        "ACTAGCAACCATGACCTTATTTGCAGCGTGCATAAGGGTGCTCATGTCGCATCATCTGAT" +
        "GCTATCATGACCCGATGTTTAGCTGTTCATGATTGCTTTTGTAAATCTGTTAATTGGAAT" +
        "TTAGAATACCCCATTATTTTAAATGAGGTTAGTGTTAATACCTCCTGTAGGTTATTGCAG" +
        "CGCGTAATGTTTAGGGCTGCGATGCTATGCAATAGGTATGATGTGTGTTATGACATTGGC" +
        "AACCCTAAGGGTCTTGCCTGTGTCAAAGGATATGATTTTAAGTTTTATGATGCCTCCCCT" +
        "GTTGTTAAGTCTGTTAAACAG-TTTGTTTATAAGTACGAGGCACATAAAGACCAATTTTT" +
        "AGATGGTCTATGTATGTTCTGGAACTGCAATGTGGATAAGTATCCCGCGAATGCAGTTGT" +
        "GTGTAGGTTTGACACGCGCGTGTTAAACAAATTAAATCTCCCTGGCTGTAATGGTGGTAG" +
        "TCTGTATGTTAATAAACATGCATTCCACACTAGCCCTTTTACCCGGGCTGCATTCGAGAA" +
        "TTTGAAGCCTATGCCTTTCTTCTATTACTCAGATACACCTTGTGTGTATATGGAAGGCAT" +
        "GGAATCTAAGCAGGTAGATTACGTCCCATTAAGGAGCGCTACATGCATTACAAGATGCAA" +
        "TTTAGGTGGCGCTGTTTGCTTAAAACATGCTGAGGAGTATCGTGAGTACCTTGAGTCTTA" +
        "CAATACGGCAACCACAGCGGGTTTTACTTTTTGGGTCTATAAGACTTTTGATTTTTATAA" +
        "CCTTTGGAACACTTTTACTAGGCTCCAAAGTTTAGAAAATGTAGTGTATAATTTGGTTAA" +
        "TGCTGGACACTTTGATGGCCGTGCGGGCGAACTGCCATGTGCCATTATAGGTGAGAAAGT" +
        "CATTGCCAAGATTCAAAATGAGGATGTCGTGGTCTTTAAAAATAACACGCCATTCCCTAC" +
        "TAATGTGGCTGTCGAATTATTTGCTAAGCGCAGTATTCGCCCCCACCCAGAGCTTAAGCT" +
        "CTTTAGAAATTTGAATATTGACGTGTGCTGGAGTCACGTCCTTTGGGATTATGCTAAGGA" +
        "TAGTGTGTTTTGCAGTTCGACGTATAAGGTCTGCAAATACACAGATTTACAGTGCATTGA" +
        "AAGCTTGAATGTACTTTTTGATGGTCGTGATAATGGTGCCCTTGAAGCTTTTAAGAAGTG" +
        "CCGGAATGGCGTCTACATTAACACGACAAAAATTAAAAGTCTGTCGATGATTAAAGGCCC" +
        "ACAACGTGCCGATTTGAATGGCGTAGTTGTGGAGAAAGTTGGAGATTCTGATGTGGAATT" +
        "TTGGTTTGCTATGAGGAGAGACGGTGACGATGTTATCTTCAGCCGTATAGAGAGCCTTGA" +
        "ACCGAGCCATTACCGGAGCCCACAAGGTAATCCGGGTGGTAATCGCGTGGGTGATCTCAG" +
        "CGGTAATGAAGCTCTAACACGTGGCACTATCTTTACTCACAGCAGATTTTTATCCTCTTT" +
        "CGCACCTCGATCAGAGATGGAGAAAGATTTTATGGATTTAGATGAAGACGTGTTCGTTGC" +
        "AAAATATAGTTTACAGGACTACGCGTTTGAACACGTTGTTTATGGTAGTTTTAACCAGAA" +
        "AATTATTGGAGGTTTGCATTTGCTTATTGGCTTAGCCCGTAGGCAGCGAAAATCCAACCT" +
        "GGTTATTCAAGAGTTCGTCCCGTACGACTCTAGCATCCACTCATACTTTATCACTGACGA" +
        "GAACAGTGGTAGTAGTAAGAGTGTGTGCACTGTTATTGATTTGTTGTTAGATGATTTTGT" +
        "GGACATTGTAAAGTCCCTGAATCTAAATTGTGTGAGTAAGGTTGTTAATGTGAATGTTGA" +
        "TTTTAAAGATTTC-CAGTTTATGTTGTGGTGTAATGAGGAGAAGGTCATGACTTTCTATC" +
        "CTCGTTTGCAGGCTGCTGCTGATTGGAAGCCTGGTTATGTTATGCCTGTATTATACAAGT" +
        "ATTTGGAATCTCCATTGGAAAGAGTAAATCTCTGGAATTATGGCAAGCCGATTACTTTAC" +
        "CTACAGGATGTTTGATGAATGTTGCCAAGTATACTCAATTATGTCAATATTTGAATACTA" +
        "CAACAATAGCAGTTCCGGCTAACATGCGTGTCTTACACCTTGGTGCTGGTTCTGATAAGG" +
        "GTGTTGCCCCTGGTTCTGCAGTTCTTAGGCAGTGGTTACCAGCGGGCAGTATTCTTGTAG" +
        "ATAATGATGTGAATCCATTTGTGAGTGACAGCGTTGCCTCATATTATGGAAATTGTATAA" +
        "CCTTACCCTTTGATTGTCAGTGGGATCTGATAATTTCTGATATGTACGACCCTCTTACTA" +
        "AGAACATTGGGGAGTACAATGTGAGT-AAAGATGGATTCTTTACTTACCTCTGTCA-TTT" +
        "AATTTGTGACAAGTTGGCTCTGGGTGGCAGTGTTGCCATAAAAATAACAGAGTTTTCTTG" +
        "GAACGCCGATTTATATAGTTTAATGGGGAAGTTTGCGTTTTGGACTATCTTTTGCACCA-" +
        "ACGTAAATGCCTCTTCAAGTGAAGGATTTTTGATTGGCATAAATTGGTTGAATAGAACCC" +
        "GTACTGAGATTGATGGTAAAACCATGCATGCCAATTATTTGTTTTGGAGGAATAGTACAA" +
        "TGTGGAATGGAGGGGCTTACAGTCTTTTTGATATGAGTAAGTTTCCCTTGAAAGCTGCTG" +
        "GTACGGCTGTCGTTAGCCTTAAACCAGACCAAATAAATGACTTAGTCCTCTCTTTGATTG" +
        "AGAAGGGCAGGTTATTAGTGCGTGATACACGCAAAGAAGTTTTTGTTGGCGATAGCCTGG" +
        "TAAATGTTAAATAAATCTATACTTGTCATGGCTGCGAAAATGGCCTTTGCTGATAAGCCT" +
        "AATCATTTCATAAACTTTCCTTTAGCCCAATTTAGTGGCTTTATGGGTAAGTATTTAAAG" +
        "TTCCAGTCTCAACTTGGGGAAATGGGTTTAGACTGTATATTACAAAAAGTACCGCATGTT" +
        "AGTATTACTATGCTTGACATAAGAGCAGAACAATACAAACAGGTGGAATTTGCAATACAA" +
        "GAAATATTAGATGATTTGGCGGCATATGAGGGAGATATTGTCTTTGACAACCCCCACATG" +
        "CTTGGCAGATGCCTTGTTCTTGATGTTAATGGATTTGAAGAGTTGCATGAAGATATTGTT" +
        "GAAATTCTCCGCAGAAGGGGTTGCACTGCAGACCAATCCAGAGATTGGATTCCGCATTGC" +
        "ACTGTGGCCCAATTTGTTGAAGAAAAAGAAATAAATGCGATGCAATTCTATTATAAATTA" +
        "CCCTTCTACCTCAAGCATAATAACATATTAATGGATTCTAGGCTTGAGCTTGTGAAGATA" +
        "GGTTCTTCCAAAATAGACGGGTTTTATTGTAGCGAGCTGAGTATTGGTTGTGGTTAGAGA" +
        "CTTTGTTATAAGCCTCCAACACCC-AAATTCAGCGATATATTTGGCTATTGCTGCATAGA" +
        "GAAAATACGTGTTGACTTAGAAATTGGCGATATAGGCCTAGCTAAGTTACCACTATCAAA" +
        "GAAAGACCTATTTCTTTAGATATGTGCATGATAATAGTATCTATTTTCGTATCGTATGTA" +
        "GAATGAAGGGTTTTATTTGTTGATTTGTTTTTACACTATTAGTGTAATAAACTTATTATT" +
        "TTGTTGAAATGGGCAGAATGTGCGTAGCTATGGCTCCTTGCACACTGCTTTTGCTGCTTG" +
        "TTTGTCAGCTGGTGTTTGGGTTCAATGAACCTCTTAACATCG-TTTCACATTTAAATGAT" +
        "GACTGGTTTCTA-TTTGGTGACAGTCGTTCTGACTGTACCTATGTAGAAAATAACGGTCA" +
        "CCCGAAATTAGATTGGCTAGACCTTGACCCGCAATTGTGTAATTCAGGAA-GGATTTCCG" +
        "CAAAGAGTGGTAACTCTCTCTTTAGGAGTTTCCATTTTACTGATTTTTACAACTACACAG" +
        "GAGAAGGCGACCAAATTATATTTTATGAAGGAGTTAATTTTAGTCCCAGCCATGGCTTTA" +
        "AATGCCTGGCTTACGGAGATAAT-AAAAGATGGATGGGCAATAAAGCTCGATTTTATGCC" +
        "CTAGTGTATGAGAAGATGGCCCATTATAGGAGTCTATCTTTTGTTAATGTTGCTTATGCC" +
        "TATGGAGGTAATGCTAGGCCTACCTCCATTTGCAAGGACAATAAGTTAACACTCAACAAT" +
        "CCCACCTTCATTTCGAAGGAGTCCAATCATGTTGATTATTACTATGAGAGTGAGGCTAAT" +
        "TTCACACTACAAGGTTGTGATGAATTTATAGTACCGCTCTGCGTTTTTAATGGCCGTTCC" +
        "AAGGGCAGCTCTTCGGACCCTGCCAATAAATATTATACAGACTCTCAGAGTTACTATAAT" +
        "ATTGATACT").build();
                        
    PlacedRead CVGWB15T06B037761RM = DefaultPlacedRead.createBuilder(contigConsensus, "CVGWB15T06B037761RM", 
            "AAGTTTAATACTGATAATAAGGTTATATACACCACAGAAGTGGCTTCAAAGCTTAATTTT" +
            "AAGTTGTGTTGTTTGGCCTTTAAGAATGCTTTACAGACGTTTAATTGGAGTGTTGTGTAC" +
            "AGGGGCTTCTTTCTAGTGGCAACAGTCTTTTTATTATGG-TTTAACTTTTTGTATGCCAA" +
            "TGTTATTTTGAGTGACTTTTATTTGCCTAATATCGGATCTCTCCCTACTTTTGTGGGGCA" +
            "GATTGTTGCTTGGGTTAAGACCACATTT-GGCGTGTCAACCATCTGTGATTTTTACCATG" +
            "TGACAGATGTGGGCTATAGGAGTTCGTTTTGCAATGGAAGCATGGTATGTGAATTATGCT" +
            "TCTTAGGTTTTGACATGTTGGACAACTATGATGCCATAAATGTTGTTCAACATGTTGTGG" +
            "ATAGGCGAGTTTCTTTTGATTATATCAGCCTATGTAAATTAGTGGTCGAGCTCATTATCG" +
            "GCTACTCGCTTTATACTGTGTGCTTCTACCCACTGTTTGTCCTTATTGGAATGCAGTTGT" +
            "TGACCACATGGTTGCCTGAATTTTTTATGCTGGAGACTATGCATTGGAGCGCCCGTTTGG" +
            "TTGTGTTTGTTGCTAATATGATCCCAGCTTTTACTTTACTGCGATTTTACATCGTGGTGA" +
            "CAGCTATGTATAATGTTTATTGTCTTTGTAGACATGTTATGTATGGATGTAGTAAGCCTG" +
            "GTTGCTTGTTTTGTTATAAGAGAAACCGTAGTGTCCGTGTTAAGTGTAGCACCGTAGTTT" +
            "G" 
            , 4870, Direction.REVERSE, Range.buildRange(10,788), 790)
            .build();
   
    
    PlacedRead CVGWB47T06D1122735FMB = DefaultPlacedRead.createBuilder(contigConsensus, "CVGWB47T06D1122735FMB",             
                         "GTACCTATGTAGAAAATAACGGTCACCCGAAATTAGATTGGCTAGACCTTGACCCGCAAT" +
                         "TGTGTAATTCAGGAA-GGATTTCCGCAAAGAGTGGTAACTCTCTCTTTAGGAGTTTCCAT" +
                         "TTTACTGATTTTTACAACTACACAGGAGAAGGCGACCAAATTATATTTTATGAAGGAGTT" +
                         "AATTTTAGTCCCAGCCATGGCTTTAAATGCCTGGCTTACGGAGATAAT-AAAAGATGGAT" +
                         "GGGCAATAAAGCTCGATTTTATGCCCTAGTGTATGAGAAGATGGCCCATTATAGGAGTCT" +
                         "ATCTTTTGTTAATGTTGCTTATGCCTATGGAGGTAATGCTAGGCCTACCTCCATTTGCAA" +
                         "GGACAATAAGTTAACACTCAACAATCCCACCTTCATTTCGAAGGAGTCCAATCATGTTGA" +
                         "TTATTACTATGAGAGTGAGGCTAATTTCACACTACAAGGTTGTGATGAATTTATAGTACC" +
                         "GCTCTGCGTTTTTAATGGCCGTTCCAAGGGCAGCTC"
                         ,
            20675, Direction.FORWARD,Range.buildRange(40,553),550)
            .build();
    
    
    
    String consensusForContig928 =
    "GTACGTACCCTCTCAACTCTAAAACTCTTGTTAGTTTAAATCTAATCTAAACTTTATAAA"+
    "CGGCACTTCCTGCGTGTCCATGCCCGTGGGCTTGGTCTTGTCATAGTGCTGACATTTGTG"+
    "GTTCCTTGACTTTCGTTCTCTGCCAGTGACGTGTCCATTCGGCGCCAGCAGCCCACCCAT"+
    "AGGTTGCATAATGGCAAAGATGGGCAAATACGGTCTCGGCTTCAAATGGGCCCCAGAATT"+
    "TCCATGGATGCTTCCGAACGCATCGGAGAAGTTGGGTAACCCTGAGAGGTCAGAGGAGGA"+
    "TGGGTTTTGCCCCTCTGCTGCGCAAGAACAGAAAGTTAAAGGAAGAACTTTGGTTAATCA"+
    "CGTGAGGGTGGATTGTAGCCGGCTTCCAGCTTTGGAGTGCTGTGTTCAGTCTGCCATAAT"+
    "CCGTGATATCTTTGTAGATGAGGATCCCCAGACGGTGGAGGCCTCAACTATGATGGCATT"+
    "GCAGTTCGGTAGTGCTGTCTTGGTTATGCCATCCAAGCGCTTGTCTATTCAGGCATGGGC"+
    "TAATTTGGGTGTGCTGCCTAGAACACCAGCCATGGGGTTGTTCAAGCGCTTCTGCCTGTG"+
    "TAATACTAGGGGATGCTCTTGTGACGGCCACGTGGCTTTTCAACTCTTTATCGTTCAACC"+
    "CGATGGCGTATGCCTAGGTAATGGACATTTTATAGGCTGGTTTGTTCCAGTCACAGCCAT"+
    "ACCAGAGCATGCGAAGCAGTGGTTGCAGCCCTGGTCCATCCTTCTTCGCAAGGGTGGTAA"+
    "CAAGGGGTCTGTGGTACCCGACCACCGCCGTGCTGTAACCATGCCTGTGTATGACTTTAA"+
    "TGTGGAGGATGCTTGCGAGGAGGTTCATCTTAACCCGAAGGGTAAGTATTCTCGCAAGGC"+
    "GTATACTCTTCTTAAGGGCTATCGCGGTGTTAAACCCATCCTTTTTGTGGACCAGTATGG"+
    "TTGCGACTATACTGGATGTCTCGCCAAGGGTCTTGAGGACTATGGTGACCTTACTTTGAG"+
    "TGAGATGAAGGAGTTGTTCCCTGTGTGGCGTGACTCCTTAGATAATGAAGTAGTTGTGGC"+
    "CTGGCATGTTGATCGTGACCCTC-GGG-CTGTTATGCGTCTGCAGACTCTTGCTACTTTA"+
    "CGTAGCATTGATTATGTGGGCCAACCGACAGAAGATGTGGTGGATGGAGATGTGGTAGTG"+
    "CGTGAGCCTGCTCATCTTCTAGCAGCCGATGCCATTGTTAAAAGACTCCCCCGTTTGGTG"+
    "GAGACTATGCTGTATACGGATTCGTCCGTTACAGAATTTTGTTATAAAACCAAGCTGTGT"+
    "GATTGTGGTTTTATCACGCAGTTTGGCTATGTGGATTGTGGTGGTGACACATGCGATTTT"+
    "CGCGGATGGGTACCGGGCAAT-ATGCTGGATGGCTTTCCTTGCCCAGGGTGTAGCAAAAG"+
    "TTACATGCCATGGGAATTGGAAGCTCAATCATCAGGTGTGATCCCAGAAAGAGGTGTTCT"+
    "GTTTACTCAGAGCACTGATACGGTGAATCGTGAGGCTTATAAGCTCTACGGTCATGCTTT"+
    "TGTGCCGTTTGGTTCTGCTGTGTATTGGAGCCCTTACCCAGGTATGTGGCTTCCAGTAGT"+
    "TTGGTCTTCTGTTAAGTCATACTCTGGTTTGACTTATACAGGAGTTGTTGGTTGTAAGGC"+
    "AATAGTTCAGGAGACAGATGCTATATGCCGGTCTCTATATATGGACTATGTCCAGCATAA"+
    "GTGTGGCAATCTCGATCAGAGAGCTACTCTTGGATTGGACGATGTCTATTATAGACAATT"+
    "GCTTGTAAATAGAGGTGACTATAGTCTCCTACTTGAAAATGTGGATTTGTTTGTTAAGTG"+
    "GCGCGCTGAATTTGCTTGCAAATTCGCCACCTGCGGAGATGGTTTTGTACCTCTTCTGCT"+
    "AGATGATTTAGTGCCCCGCAGTTATTATTTAATTAAGAGTGGCCAGGCCTACACCTCGAT"+
    "GATGGTTAATTTTAGCCATGAGGTGATTGACATGTGTATGGACATGGCATTATTGTTC";
    @Test
    public void decode() throws IOException{
        final File file = getFile();
        Contig contig925 = getContig925From(file);
        assertEquals(contig_id, Integer.parseInt(contig925.getId()));
        assertEquals(contig_length, contig925.getConsensus().getLength());
        assertEquals(numberOfReads, contig925.getNumberOfReads());
        AssemblyTestUtil.assertPlacedReadCorrect(CVGWB15T06B037761RM, contig925.getPlacedReadById("CVGWB15T06B037761RM"));
        AssemblyTestUtil.assertPlacedReadCorrect(CVGWB47T06D1122735FMB, contig925.getPlacedReadById("CVGWB47T06D1122735FMB"));
    
        
    }
    @Test
    public void decodeLastConsensus() throws Exception{
        Contig contig928 = getContig928From(getFile());
        assertEquals(consensusForContig928, Nucleotides.asString(contig928.getConsensus().asList()));
    }

    protected abstract Contig getContig928From(File file) throws Exception;
    
    public File getFile() throws IOException {
        return RESOURCES.getFile(pathToFile);
    }

    
    protected abstract Contig getContig925From(File file) throws FileNotFoundException;


    
}
