package org.jcvi.jillion.assembly.tasm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.assembly.tasm.DefaultTasmFileContigDataStore;
import org.jcvi.jillion.assembly.tasm.TasmContig;
import org.jcvi.jillion.assembly.tasm.TasmContigDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.junit.Test;
public class TestAnnotationTasmParsing {
	 private static final ResourceHelper RESOURCES = new ResourceHelper(TestAnnotationTasmParsing.class);
	 
	 private final TasmContigDataStore datastore;
	 public TestAnnotationTasmParsing() throws FileNotFoundException, IOException{
		 datastore = DefaultTasmFileContigDataStore.create(RESOURCES.getFile("files/annotation.tasm"));
			
	 }
	 @Test
	 public void numberOfContigs() throws DataStoreException{
		 assertEquals(3L, datastore.getNumberOfRecords());
	 }
	 @Test
	 public void correctContigIds() throws IOException, DataStoreException{
		 assertTrue(datastore.contains("1122071329926"));
		 assertTrue(datastore.contains("1122071329927"));
		 assertTrue(datastore.contains("1122071329928"));
		 
	 }
	 
	 @Test
	 public void contigEndingIn927() throws DataStoreException{
		 String gappedConsensusOf927 = 
				 "GAATGGATGTCAATCCGACTTTACTTTTCCTGAAAGTTCCAGCGCAGAATGCCAT" +
				 "AAGCACCACATTCCCTTATACTGGAGATCCTCCATACAGCCATGGAACGGGAACA" +
				 "GGATACACCATGGACACAGTCAACAGGACACATCAATATTCAGAAAAGGGGAAAT" +
				 "GGACAACAAACACAGAGACTGGGGCACCCCAACTCAACCCAATCGATGGACCACT" +
				 "GCCCGAAGACAATGACCCAAGTGGATATGCACAAACAGACTGTGTCCTTGAAGCA" +
				 "ATGGCTTTCCTTGAAGAGTCCCACCC-AGGAATCTTTGAAAACTCGTGTCTTGAA" +
				 "ACGATGGAAGTTGTCCAACAAACAAGAATGGACAAACTGACCCAGGGTCGTCAGA" +
				 "CCTATGATTGGACTTTAAACAGGAATCAGCCGGCTGCAACTGCATTAGCCAATAC" +
				 "TATAGAGGTCTTCAGATCGAATGGTCTGACAGCTAATGAATCAGGAAGGCTGATA" +
				 "GATTTCCTCAAGGATGTGATGGAATCAATGGATAAAGAGGGAATGGAAATAACAA" +
				 "CGCACTTCCAAAGAAAGAGAAGAGTAAGAGACAACATGACCAAGAAAATGGTCAC" +
				 "ACAAAGGACAATAGGAAAGAAGAAGCAAAGGCTGAACAAGAAAAGCTATCTAATA" +
				 "AGAGCATTGACACTAAACACAATGACCAA-AGATGCTGAAAGAGGCAAATTAAAG" +
				 "AGGAGAGCAATTGCTACACCCGGAATGCAGATCAGAGGATTTGTATACTTTGTTG" +
				 "AAACATTAGCGAGGAGCATCTGTGAGAAGCTTGAACAATCTGGACTCCCAGTTGG" +
				 "AGGCAATGAAAAGAAGGCTAAACTGGCAAACGTTGTAAGAAAAATGATGACTAAT" +
				 "TCACAAGACACAGAACTCTCCTTCACTATCACCGGAGACAACACCAAATGGAATG" +
				 "AGAATCAGAACCCTAGGATGTTTCTGGCAATGATAACATACATAACAAGGAACCA" +
				 "ACCTGAGTGGTTTAGGAATGTCTTGAGCATTGCACCTATAATGTTCTCGAATAAA" +
				 "ATGGCA-AGACTAGGGAAGGGATACATGTTCGAAAGCA-AGAGCATGAAGCTTAG" +
				 "AACACAGATACCAGCAGAAATGCTAGCAAGTATTGATCTAAAATATTTCAATGAG" +
				 "TCAACAAGAAAGAAAATAGAGAAGATAAGGCCTCTTCTAATAGATGGTACAGCTT" +
				 "CATTGAGCCCTGGAATGATGATGGGCATGTTCAACATGCTAAGTACAGTTTTGGG" +
				 "AGTCTCGATTCTCAACCTAGGGCAGAAGAGGTACACCAAAACAACATATTGGTGG" +
				 "GACGGACTCCAATCCTCCGATGACTTTGCTCTTATAGTGAATGCTCCGAATCATG" +
				 "AAGGAATACAAGCAGGAGTAGATAGATTCTATAGAACCTGCAAGCTGGTCGGAAT" +
				 "AAATATGAGCAAAAAGAAGTCCTACATAAACAAGACAGGGACATTTGAATTCACA" +
				 "AGCTTTTTCTATCGCTATGGATTTGTAGCCAATTTTAGCATGGAGCTTCCCAGTT" +
				 "TTGGAGTGTCTGGGATTAATGAATCTGCTGACATGAGCATTGGAGTAACAGTGAT" +
				 "AAAGAACAACATGATAAACAATGATCTTGGACCAGCAACAGCTCAAATGGCTCTT" +
				 "CAGCTATTCATCAAGGATTACAGATACACGTATCGGTGCCACAGAGGGGACACAC" +
				 "AAATTCAAACAAGGAGGTCATTCGAGCTGAAGAAGTTGTGGGAACAAACCCGCTC" +
				 "AAAGGCAGGACTGCTGGTTTCAGATGGAGGGCCAAACTTATACAATATCCGAAAT" +
				 "CTCCACATCCCGGAGGTCTGCCTGAAATGGGAGCTGATGGACGAAGATTATCAGG" +
				 "GAAGGCTTTGTAACCCCCTGAATCCATTTGTCAGCCACAAAGAGATAGAGTCTGT" +
				 "AAACAATGCTGTGGTGATGCCAGCTCATGGCCCAGCCAAGAGCATGGAATATGAT" +
				 "GCTGTTGCTACCACGCACTCCTGGATCCCTAAGAGGAACCGCTCCATTCTCAACA" +
				 "CAAGCCAAAGGGGAATCCTTGAAGATGAACAGATGTATCAGAAATGCTGCAATCT" +
				 "ATTCGAGAAATTCTTCCCTAGCAGCTCATACAGGAGACCGGTTGGAATTTCCAGC" +
				 "ATGGTGGAGGCCATGGTTTCTAGGGCCCGAATTGATGCGAGAATTGACTTCGAAT" +
				 "CTGGACGGATTAAGAAGGAGGAGTTTGCTGAGATCATGAAGATCTGTTCCACCAT" +
				 "TGAAGAGCTCAGACGGCAGAAATAGTGAATTTAGCTTGTCCTTCATG";
		 
		 TasmContig contig = datastore.get("1122071329927");
		assertEquals(gappedConsensusOf927, contig.getConsensusSequence().toString());
	 }
}