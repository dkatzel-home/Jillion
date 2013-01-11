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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice.consensus;

import org.jcvi.common.core.assembly.util.slice.Slice;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * <code>ConsensusCaller</code> compute the
 * {@link ConsensusResult} for the given Slice.
 * @author dkatzel
 *
 *
 */
public interface ConsensusCaller {
    /**
     * compute the consensus
     * {@link Nucleotide} for the given Slice.
     * @param slice the Slice to compute the consensus for.
     * @return a {@link ConsensusResult} will never be <code>null</code>
     * @throws NullPointerException if slice is null.
     */
    ConsensusResult callConsensus(Slice<?> slice);
}
