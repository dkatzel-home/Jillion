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
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.Set;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
public class BasicChurchillWatermanConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    private static final int MAX_NUMBER_OF_BASES_TOWARDS_AMBIGUITY = 5;


    public BasicChurchillWatermanConsensusCaller(
            PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    

    @Override
    protected NucleotideGlyph getConsensus(
            ProbabilityStruct normalizedErrorProbabilityStruct,
            Slice slice) {
        final Set<NucleotideGlyph> basesUsedTowardsAmbiguity = getBasesUsedTowardsAmbiguity(normalizedErrorProbabilityStruct,
                        MAX_NUMBER_OF_BASES_TOWARDS_AMBIGUITY);
        return NucleotideGlyph.getAmbiguityFor(basesUsedTowardsAmbiguity);
        
    }
    
    

    


    
}
