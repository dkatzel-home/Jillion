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

package org.jcvi.common.core.assembly;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Rangeable;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;

/**
 * {@code AssembledReadBuilder} is a {@link Builder}
 * for {@link AssembledRead}s for a specific contig.
 * Methods in this interface can change the bases
 * of this read or shift where on the reference (or contig consensus)
 * this read lands.
 * @author dkatzel
 *
 *
 */
public interface AssembledReadBuilder<R extends AssembledRead> extends Rangeable, Builder<R>{
	
	
    /**
     * Change the reference that this read aligns to and its new
     * gapped starting offset on this new reference.
     * @param reference the new reference (or consensus) to align this
     * read to.
     * @param newOffset the new gapped start offset of this read
     * against the new reference in reference coordinate space.
     * @return this.
     * @throws NulPointerException if reference is null.
     */
    AssembledReadBuilder<R> reference(NucleotideSequence reference, int newOffset);
    /**
     * 
    * Get the gapped start offset of this read
     * against the new reference in reference coordinate space.
     */
    long getBegin();
    /**
     * Get the read id.
     * @return this
     */
    String getId();
    /**
     * Change the gapped start offset of this read to a new
     * value on the same reference.
     * @param newOffset the new gapped start offset.
     * @return this.
     */
    AssembledReadBuilder<R> setStartOffset(int newOffset);
    /**
     * Change the gapped start offset of this read
     * by shifting it to the given number of gapped
     * bases.
     * @param numberOfBases the number of gapped bases
     * this read should get shifted by. A positive value
     * will increase this read's gapped start offset'
     * a negative value will decrease this read's
     * gapped start offset.  A value of 0 will 
     * cause no change.
     * @return this.
     */
    AssembledReadBuilder<R> shift(int numberOfBases);

    /**
     * @return the clearRange
     */
    Range getClearRange();

    AssembledReadBuilder<R> setClearRange(Range updatedClearRange);
    /**
     * Get the {@link Direction} of this read.
     * @return the {@link Direction} will never be null.
     */
    Direction getDirection();

    /**
     * @return the ungappedFullLength
     */
    int getUngappedFullLength();
    /**
     * 
    * {@inheritDoc}
    * <p/>
    * Creates a new {@link AssembledRead} instance using the current
    * values given to this builder.
     */
    @Override
    R build();
    /**
     * Modify the gapped basecall sequence of this read
     * to change <strong>only the gaps</strong> of the given subsequence.
     * Sometimes, assembly errors or new alignments mean that parts of underlying 
     * reads may have to get re-gapped to make better alignments.  This method
     * allows partial sequences to get modified so that their gapped sequence
     * can be modified without changing the non-gap bases.
     * @param gappedValidRangeToChange the subsequence of the read to change
     * in gapped <strong>valid range</strong> coordinate system 
     * (only has long as the length of the read).
     * @param newBasecalls the new gapped {@link NucleotideSequence} to replace the bases previously 
     * existing in the {@code gappedValidRangeToChange} range.  The new basecalls
     * do not have to have the same length as the old values but must have the same 
     * ungapped sequence.
     * @return this.
     * @throws IllegalArgumentException if the ungapped version of the newBasecalls 
     * does not match the ungapped version of the bases to be replaced.
     */
    AssembledReadBuilder<R> reAbacus(Range gappedValidRangeToChange,
            NucleotideSequence newBasecalls);
    /**
    * Get the gapped length of this read that
    * aligns to the reference.
     */
    long getLength();
    /**
    * Get the gapped end coordinate of this read that
    * aligns to the reference.
     */
    long getEnd();
    /**
     * 
    * {@inheritDoc}
    * <p/>
    * Get the gapped start and end offsets of this
    * read against this reference as a Range.
     */
    Range asRange();

    /**
     * @return the basesBuilder
     */
    NucleotideSequenceBuilder getNucleotideSequenceBuilder();
    /**
     * Get the current gapped bases of this read 
     * as a NucleotideSequence.  This sequence is immutable
     * and not backed by this builder so any if future calls
     * to this class modify the basecalls of this
     * read, then the NucleotideSequence that was
     * previously returned by this method will be out
     * of sync.
     * @return a NucleotideSequence of the current (possibly modified)
     * basecalls of this read; never null.
     */
    NucleotideSequence getCurrentNucleotideSequence();

}
