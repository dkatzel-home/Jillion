package org.jcvi.jillion.align.pairwise;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.jcvi.jillion.align.AminoAcidSequenceAlignmentBuilder;
import org.jcvi.jillion.align.pairwise.AminoAcidNeedlemanWunschAligner;
import org.jcvi.jillion.align.pairwise.AminoAcidPairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.AminoAcidPairwiseSequenceAlignmentImpl;
import org.jcvi.jillion.align.pairwise.AminoAcidScoringMatrix;
import org.jcvi.jillion.align.pairwise.PairwiseSequenceAlignmentWrapper;
import org.jcvi.jillion.align.pairwise.blosom.BlosomMatrices;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.junit.Test;

public class TestAminoAcidNeedlemanWunschAligner {

	@Test
	public void exampleFromBook(){
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		AminoAcidSequence subject = new AminoAcidSequenceBuilder("HEAGAWGHEE")
									.build();
		AminoAcidSequence query = new AminoAcidSequenceBuilder("PAWHEAE")
										.build();
		AminoAcidPairwiseSequenceAlignment expected = createExpectedAlignment("--P-AW-HEAE","HEAGAWGHE-E", 1F);
		
		AminoAcidPairwiseSequenceAlignment actual = AminoAcidNeedlemanWunschAligner.align(
				query, subject, blosom50, -8, -8);
	
		assertEquals(expected, actual);
	}
	
	protected AminoAcidPairwiseSequenceAlignment createExpectedAlignment(String gappedSeq1, String gappedSeq2, float score){
		AminoAcidSequenceAlignmentBuilder builder = new AminoAcidSequenceAlignmentBuilder();
		AminoAcidSequence seq1 = new AminoAcidSequenceBuilder(gappedSeq1).build();
		AminoAcidSequence seq2 = new AminoAcidSequenceBuilder(gappedSeq2).build();
		Iterator<AminoAcid> seq1Iter = seq1.iterator();
		Iterator<AminoAcid> seq2Iter = seq2.iterator();
		
		while(seq1Iter.hasNext()){
			AminoAcid base1 = seq1Iter.next();
			AminoAcid base2 = seq2Iter.next();
			if(base1==base2){
				builder.addMatch(base1);
			}else if (base1==AminoAcid.Gap || base2 == AminoAcid.Gap){
				builder.addGap(base1,base2);
			}else{
				builder.addMismatch(base1, base2);
			}
		}
		if(seq2Iter.hasNext()){
			throw new IllegalArgumentException("seq2 is longer than seq1");
		}
		return new AminoAcidPairwiseSequenceAlignmentImpl(
				PairwiseSequenceAlignmentWrapper.wrap(builder.build(), score));
		
	}
}