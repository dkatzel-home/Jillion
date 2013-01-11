package org.jcvi.common.core.seq.fasta.aa;

import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.jillion.core.internal.seq.fasta.aa.UnCommentedAminoAcidSequenceFastaRecord;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestUnCommentedAminoAcidSequenceFastaRecord extends AbstractTestAminoAcidSequenceFastaRecord{

	@Override
	protected AminoAcidSequenceFastaRecord createRecord(String id,
			AminoAcidSequence seq, String optionalComment) {
		return new UnCommentedAminoAcidSequenceFastaRecord(id,seq);
	}

	@Test
	public void commentsShouldAlwaysReturnNull(){
		assertNull(sut.getComment());
	}
}
