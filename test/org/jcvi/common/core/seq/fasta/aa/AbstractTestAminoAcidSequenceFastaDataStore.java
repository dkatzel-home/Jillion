package org.jcvi.common.core.seq.fasta.aa;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.common.core.seq.fasta.aa.impl.UnCommentedAminoAcidSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestAminoAcidSequenceFastaDataStore {

	private final AminoAcidSequenceFastaRecord firstRecord = new UnCommentedAminoAcidSequenceFastaRecord(
			"ABN50481.1",
			new AminoAcidSequenceBuilder("MKAIIVLLLVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGAIPLTTTPTKSHFANLKGTKTRGKLCPTCFN" +
			"CTDLDVALGRPMCVGITPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYEKIRLSTQNVIDAEKAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPRDNNKTATNPLTVEVPHICTKEEDQITVWGFHSDNKTQMKNLYGDS" +
			"NPQKFTSSANGITTHYVSQIGGFPDQTEDGGLPQSGRIVVDYMVQKPGKTGTIVYQRGILLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSEDEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMIAIFIVYMISRDNVSCSICL").build());
	
	private final AminoAcidSequenceFastaRecord middleRecord = new UnCommentedAminoAcidSequenceFastaRecord(
			"ABR15984.1",
			new AminoAcidSequenceBuilder("MKAIIVLLMVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGVIPLTTTPTKSYFANLKGTRTRGKLCPDCLN" +
			"CTDLDVALGRPMCVGTTPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYENIRLSTQNVIDAENAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPKDNNKNATNPLTVEVPYICTEGEDQITVWGFHSDNKTQMKNLYGDS" +
			"NPQKFTSSANGVTTHYVSQIGGFPAQTEDGGLPQSGRIVVDYMVQKPRKTGTIVYQRGVLLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSEDEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMLAIFIVYMVSRDNVSCSICL").build());
	
	private final AminoAcidSequenceFastaRecord lastRecord = new UnCommentedAminoAcidSequenceFastaRecord(
			"EPI159954",
			new AminoAcidSequenceBuilder("MKAIIVLLMVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGVIPLTTTPTKSYFANLKGTRTRGKLCPDCLN" +
			"CTDLDVALGRPMCVGTTPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYENIRLSTQNVIDAEKAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPKDNNKNATNPLTVEVPYICTEGEDQITVWGFHSDDKTQMKNLYGDS" +
			"NPQKFTSSANGVTTHYVSQIGGFPDQTEDGGLPQSGRIVVDYMMQKPGKTGTIVYQRGVLLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSENEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMLAIFIVYMVSRDNVSCSICL").build());
	
	private final AminoAcidSequenceFastaDataStore sut;
	
	public AbstractTestAminoAcidSequenceFastaDataStore() throws Exception{
		ResourceFileServer resources = new ResourceFileServer(AbstractTestAminoAcidSequenceFastaDataStore.class);
		sut = create(resources.getFile("files/example.aa.fasta"));
	}
	
	protected abstract AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception;
	
	@Test
	public void numberOfRecords() throws DataStoreException{
		assertEquals(13L, sut.getNumberOfRecords());
	}
	
	@Test
	public void idIterator() throws DataStoreException{
		Iterator<String> expectedIterator = Arrays.asList(
				"ABN50481.1",
				"EPI55973",
				"ABL77156.1",
				"EPI51649",
				"ACF54202.1",
				"ABR15984.1",
				"EPI62257",
				"ACF54180.1",
				"EPI159930",
				"ABL76749.1",
				"EPI50928",
				"ACF54213.1",
				"EPI159954"
				).iterator();
		
		StreamingIterator<String> actual = sut.idIterator();
		try{
			while(expectedIterator.hasNext()){
				assertTrue(actual.hasNext());
				assertEquals(expectedIterator.next(), actual.next());
			}
			assertFalse(actual.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actual);
		}
	}
	
	@Test
	public void getFirstRecord() throws DataStoreException{
		assertEquals(firstRecord, sut.get("ABN50481.1"));
	}
	
	@Test
	public void getMiddleRecord() throws DataStoreException{
		assertEquals(middleRecord, sut.get("ABR15984.1"));
	}
	
	@Test
	public void getLastRecord() throws DataStoreException{
		assertEquals(lastRecord, sut.get("EPI159954"));
	}
	
	@Test
	public void iterator() throws DataStoreException{
		//we aren't going to check each record.
		//we will assume that if the order
		//is correct and our first, middle
		//and last records are correct and in the 
		//correct order then the other records
		//in between are correct as well.
		StreamingIterator<AminoAcidSequenceFastaRecord> iter= sut.iterator();
		
		try{
			assertTrue(iter.hasNext());
			assertEquals(firstRecord, iter.next());
			assertTrue(iter.hasNext());
			//skip to the next record we know about
			for(int i=0; i<4; i++){
				assertTrue(iter.hasNext());
				assertNotNull(iter.next());
			}
			assertEquals(middleRecord, iter.next());
			assertTrue(iter.hasNext());
			//skip to the last record
			for(int i=0; i<6; i++){
				assertTrue(iter.hasNext());
				assertNotNull(iter.next());
			}
			assertEquals(lastRecord, iter.next());
			assertFalse(iter.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
}