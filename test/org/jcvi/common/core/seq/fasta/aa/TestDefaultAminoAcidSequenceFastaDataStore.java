package org.jcvi.common.core.seq.fasta.aa;

import java.io.File;

import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.core.internal.seq.fasta.aa.DefaultAminoAcidSequenceFastaDataStore;

public class TestDefaultAminoAcidSequenceFastaDataStore extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestDefaultAminoAcidSequenceFastaDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return DefaultAminoAcidSequenceFastaDataStore.create(fastaFile);
	}

}
