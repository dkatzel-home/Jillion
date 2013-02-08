package org.jcvi.jillion.assembly.asm;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.jcvi.jillion.assembly.asm.AsmVisitor2.UnitigLayoutType;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public abstract class AbstractAsmContigBuilder implements AsmContigVisitor{

	private final Map<String, Range> validRanges;
	private final DataStore<NucleotideSequence> fullLengthSequenceDatastore;
	
	private final AsmContigBuilder builder;
	
	
	
	public AbstractAsmContigBuilder(
			String id,
			NucleotideSequence consensus,
			boolean isDegenerate,
			DataStore<NucleotideSequence> fullLengthSequenceDatastore,
			Map<String, Range> validRanges) {
		this.fullLengthSequenceDatastore = fullLengthSequenceDatastore;
		this.validRanges = validRanges;
		
		builder = DefaultAsmContig.createBuilder(id, consensus, isDegenerate);
	}

	/**
	 * Ignored by default.  This method
	 * may be overridden to handle variant records
	 * (example, dephasing diploid sequence by collecting
	 * all the linked variants and modifying the builder
	 * as a post process.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public void visitVariance(Range range, long numberOfSpanningReads,
			long anchorSize, long internalAccessionId,
			long accessionForPhasedVariant,
			SortedSet<VariantRecord> variantRecords) {
		//default to no-op
		
	}

	@Override
	public void visitReadLayout(char readType, String externalReadId,
			DirectedRange readRange, List<Integer> gapOffsets) {
        try {
            NucleotideSequence fullLengthSequence = fullLengthSequenceDatastore.get(externalReadId);
            Range clearRange = validRanges.get(externalReadId);
            if(clearRange==null){
                throw new IllegalStateException("do not have clear range information for read "+ externalReadId);
            }
           
            NucleotideSequenceBuilder validBases = new NucleotideSequenceBuilder(fullLengthSequence)
            											.trim(clearRange);
            if(readRange.getDirection() == Direction.REVERSE){
                validBases.reverseComplement();
            }
            validBases = AsmUtil.computeGappedSequence(validBases, gapOffsets);
            builder.addRead(externalReadId, validBases.toString(),
                    (int)readRange.asRange().getBegin(),readRange.getDirection(),
                    clearRange, 
                    (int)fullLengthSequence.getLength(),
                    false);
        } catch (DataStoreException e) {
            throw new IllegalStateException(
            		"error getting read id "+ externalReadId 
                    + " from frg file", e);
        }
		
	}
	/**
	 * Ignored by default since this class
	 * only works with contig data.
	 * {@inheritDoc}
	 */
	@Override
	public void visitUnitigLayout(UnitigLayoutType type,
			String unitigExternalId, DirectedRange unitigRange,
			List<Long> gapOffsets) {
		//no-op
		
	}

	@Override
	public void visitIncompleteEnd() {
		//no-op
		
	}

	@Override
	public final void visitEnd() {
		visitContig(builder);	
	}

	protected abstract void visitContig(AsmContigBuilder builder);
}
