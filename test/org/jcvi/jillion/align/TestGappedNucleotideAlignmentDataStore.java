package org.jcvi.jillion.align;

import java.io.IOException;

import org.jcvi.jillion.align.GappedNucleotideAlignmentDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestGappedNucleotideAlignmentDataStore {
	
	private final NucleotideSequenceDataStore sut;
	
	public TestGappedNucleotideAlignmentDataStore() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestGappedNucleotideAlignmentDataStore.class);
	    sut = GappedNucleotideAlignmentDataStore.createFromAlnFile(resources.getFile("files/example.aln"));
	}
	@Test
	public void getNumberOfRecords() throws DataStoreException{
		assertEquals(7, sut.getNumberOfRecords());
	}
	
	@Test
	public void getFirstRecord() throws DataStoreException{
		String id = "gi|304633245|gb|HQ003817.1|";
		NucleotideSequence expectedSequence  = new NucleotideSequenceBuilder(120)
		.append("CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG")
		.append("GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG")		
		.append("AAGGTATATTATTGATGATG")
		.build();
		assertEquals(expectedSequence, sut.get(id));
	}
	
	@Test
	public void getLastRecord() throws DataStoreException{
		String id = "gi|58177684|gb|AY601635.1|";
		NucleotideSequence expectedSequence  = new NucleotideSequenceBuilder(120)
		.append("CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG")
		.append("GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG")		
		.append("AAGGTATATTATTGATGATG")
		.build();
		assertEquals(expectedSequence, sut.get(id));
	}
	
	@Test
	public void getMiddleRecordWithSNPs() throws DataStoreException{
		String id = "gi|9626158|ref|NC_001405.1|";
		NucleotideSequence expectedSequence  = new NucleotideSequenceBuilder(120)
		.append("CATCATCA-TAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG")
		.append("GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG")		
		.append("AAGGTATATTAT-GATGATG")
		.build();
		assertEquals(expectedSequence, sut.get(id));
	}
}