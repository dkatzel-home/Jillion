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

package org.jcvi.common.core.seq.trim;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.trim.DefaultPrimerTrimmer;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestInternalPrimerHit {

    private final DefaultPrimerTrimmer sut = new DefaultPrimerTrimmer(13, .9f);
    
  																																																																																																																																							
    private final NucleotideSequence sequence = new NucleotideSequenceBuilder(
            "AGGAAAAATTTTTGATTGGATGTCATCCGACTTTACTTTTCTTGAAGTTCCAGCGCAAAATGCCATAAGCACCACATTCCCATATACTGGAGATCCTCCATACAGCCATGGAACAGGAACAGGATACACCATGGACACAGTTAACAGAACACATCAATATTCAGAAAAGGGGAAATGGACAACAAACTCAGAGACTGGAGCCCCCCAACTTAACCCAATTGATGGACCACTGCCCGAGGACAATGAGCCAAGTGGATATGCACAAACGGACTGTGTCCTTGAAGCAATGGCTTTCCTTGAAGAGTCCCACCCAGGAATCTTTGAAAACTCGTGTCTTGAAACGATGGAAGTTGTCCAACAAACAAGAGTGGACAAGTTGACCCAAGGCCGTCAGACCTATGATTGGACACTAAACAGGAACCAGCCGGCTGCAACTGCATTAGCTAATACTATAGAGGTCTTCAGATCGAACGGTCTGACAGCTAATGAATCAGGGAGACTAATAGATTTTCTCAAGGATGTGATGGAATCAATGGATAAAGAGGAAATGGAAATAACAACACACTTCCAGGTCATAGCTGTTTCCTAAACA").build();

    private final NucleotideSequence forwardPrimer = new NucleotideSequenceBuilder("TGTAAAACGACGGCCAGTCRAAAGCAGGCAAACCAT").build();
    private final NucleotideSequence reversePrimer = new NucleotideSequenceBuilder("CAGGAAACAGCTATGACCTGGAARTGYGTTGTKATTTCCATY").build();


    @Test
    public void trim(){
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(forwardPrimer, reversePrimer);
    
        Range expectedRange = Range.buildRange(0, 545);
        Range actualRange = sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
}
