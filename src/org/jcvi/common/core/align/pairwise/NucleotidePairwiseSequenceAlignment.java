package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code NucleotidePairwiseSequenceAlignment}
 * is a marker interface for a {@link PairwiseSequenceAlignment}
 * for {@link Nucleotide}s.
 * @author dkatzel
 *
 */
public interface NucleotidePairwiseSequenceAlignment extends PairwiseSequenceAlignment<Nucleotide, NucleotideSequence>{

}
