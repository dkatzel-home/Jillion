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
 * Created on Jan 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.nuc;

import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;

public class DefaultNucleotideSequenceFastaRecord extends AbstractNucleotideSequenceFastaRecord{

   
    public DefaultNucleotideSequenceFastaRecord(String identifier, Sequence<Nucleotide> sequence){
        super(identifier, Nucleotides.convertToString(sequence.decode()));
    }
    public DefaultNucleotideSequenceFastaRecord(String identifier, String comments, Sequence<Nucleotide> sequence){
        super(identifier, comments,Nucleotides.convertToString(sequence.decode()));
    }
    public DefaultNucleotideSequenceFastaRecord(String identifier, String comments, List<Nucleotide> sequence){
        super(identifier, comments,Nucleotides.convertToString(sequence));
    }
    public DefaultNucleotideSequenceFastaRecord(String identifier,  List<Nucleotide> sequence){
        this(identifier, null,sequence);
    }
    /**
     * @param identifier
     * @param sequence
     */
    public DefaultNucleotideSequenceFastaRecord(int identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultNucleotideSequenceFastaRecord(int identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }

    /**
     * @param identifier
     * @param sequence
     */
    public DefaultNucleotideSequenceFastaRecord(String identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultNucleotideSequenceFastaRecord(String identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }

    @Override
    protected CharSequence decodeNucleotides() {

        StringBuilder result = new StringBuilder();
        for(Nucleotide glyph : getValue().decode()){
            result.append(glyph.getCharacter());
        }
        return result;
    }

    @Override
    protected NucleotideSequence encodeNucleotides(
            CharSequence sequence) {
        return new DefaultNucleotideSequence( Nucleotides.getNucleotidesFor(sequence));
    }


   
}
