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
 * Created on Mar 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nt;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.AbstractResidueSequence;

/**
 * {@code DefaultNucleotideSequence} is the default
 * implementation of a {@link NucleotideSequence}.  
 * Depending on the {@link NucleotideCodec} used,
 * the nucleotides can be encoded as 4 bits, 2 bits
 * or some other efficient manner.
 * @author dkatzel
 *
 *
 */
final class DefaultNucleotideSequence extends AbstractResidueSequence<Nucleotide> implements NucleotideSequence{

    /**
     * {@link NucleotideCodec} used to decode the data.
     */
    private final NucleotideCodec codec;
    /**
     * Our data.
     */
    private final byte[] data;
    

    
    public static NucleotideSequence create(Collection<Nucleotide> nucleotides){
        return new DefaultNucleotideSequence(nucleotides);
    }
    public static DefaultNucleotideSequence createACGTN(Collection<Nucleotide> nucleotides){
        return new DefaultNucleotideSequence(nucleotides, ACGTNNucloetideCodec.INSTANCE);
    }
    public static DefaultNucleotideSequence createNoAmbiguities(Collection<Nucleotide> nucleotides){
        return new DefaultNucleotideSequence(nucleotides, NoAmbiguitiesEncodedNucleotideCodec.INSTANCE);
    }
    public static NucleotideSequence createGappy(Collection<Nucleotide> nucleotides){
        return new DefaultNucleotideSequence(nucleotides, DefaultNucleotideCodec.INSTANCE);
    }
    static NucleotideSequence create(Collection<Nucleotide> nucleotides,NucleotideCodec codec){
        if(codec ==null){
            throw new NullPointerException("codec can not be null");
        }
        return new DefaultNucleotideSequence(nucleotides, codec);
    }
    private DefaultNucleotideSequence(Collection<Nucleotide> nucleotides){
        this(nucleotides,NucleotideCodecs.getNucleotideCodecFor(nucleotides));
   
    }
    
    private DefaultNucleotideSequence(Collection<Nucleotide> nucleotides,NucleotideCodec codec ){
        this.codec =codec;
        this.data = codec.encode(nucleotides);
   
    }

    
    @Override
    public List<Integer> getGapOffsets() {
    	return codec.getGapOffsets(data);
    }

    @Override
    public List<Nucleotide> asList() {
    	return codec.decode(data);
    }

    @Override
    public Nucleotide get(int index) {     
    	return codec.decode(data, index);
    }

    @Override
    public long getLength() {
    	return codec.decodedLengthOf(data);
    }
    @Override
    public boolean isGap(int index) {
    	return codec.isGap(data, index);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + toString().hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof NucleotideSequence)){
            return false;
        }
        NucleotideSequence other = (NucleotideSequence) obj;
       return toString().equals(other.toString());
    }
    @Override
    public String toString() {
        return codec.toString(data);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps() {
    	return codec.getNumberOfGaps(data);
    }
	@Override
	public Iterator<Nucleotide> iterator() {
		return codec.iterator(data);
	}
	@Override
	public Iterator<Nucleotide> iterator(Range range) {
		return codec.iterator(data,range);
	}

	
}
