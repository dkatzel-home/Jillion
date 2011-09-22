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

package org.jcvi.common.core.symbol.residue.nuc;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.util.Builder;

/**
 * {@code NucleotideSequenceBuilder}  is a way to
 * construct a {@link NucleotideSequence}
 * similar to how a {@link StringBuilder} can be used
 * to create a String.  The contents of the NucleotideSequence
 * can be changed by method calls.  This class
 * is not thread safe.
 * @author dkatzel
 *
 *
 */
public final class NucleotideSequenceBuilder implements Builder<NucleotideSequence>{

    private final StringBuilder builder;
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides.
     */
    public NucleotideSequenceBuilder(){
        this.builder = new StringBuilder();
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     * @param sequence the initial nucleotide sequence.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder(Iterable<Nucleotide> sequence){
        this(Nucleotides.asString(sequence));
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     * @param sequence the initial nucleotide sequence.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder(String sequence){
        assertNotNull(sequence);
        this.builder = new StringBuilder(sequence);
    }
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder append(Iterable<Nucleotide> sequence){
        assertNotNull(sequence);
        return append(Nucleotides.asString(sequence));
    }
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder append(String sequence){
        assertNotNull(sequence);
        builder.append(sequence);
        return this;
    }
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    public NucleotideSequenceBuilder insert(int offset, String sequence){
        assertNotNull(sequence);
        if(offset<0){
            throw new IllegalArgumentException("offset can not have negatives coordinates: "+ offset);
        }
        if(offset> getLength()){
            throw new IllegalArgumentException(
                    String.format("offset can not start beyond current length (%d) : %d", getLength(),offset));
        }   
        builder.insert(offset, sequence);
        return this;
    }
    private void assertNotNull(Object sequence) {
        if(sequence ==null){
            throw new NullPointerException("sequence can not be null");
        }
    }
    /**
     * Get the current length of the mutable
     * sequence. 
     * @return the current length
     * of the nucleotide sequence.
     */
    public int getLength(){
        return builder.length();
    }
    /**
     * Deletes the nucleotides from the given range of this 
     * partially constructed NucleotideSequence.  If the given
     * range is empty, then the nucleotideSequence will not
     * be modified. If the range extends beyond the currently
     * built sequence, then this will delete until the end of
     * the sequence.
     * @param range the range to delete can not be null.
     * @return this.
     * @throws NullPointerException if range is null.
     * @throws IllegalArgumentException if range's start is negative
     * or greater than this nucleotide sequence's current length.
     */
    public NucleotideSequenceBuilder delete(Range range){
        if(range ==null){
            throw new NullPointerException("range can not be null");
        }
        if(!range.isEmpty()){
            int start = (int)range.getStart();
            if(start<0){
                throw new IllegalArgumentException("range can not have negatives coordinates: "+ start);
            }
            if(start> getLength()){
                throw new IllegalArgumentException(
                        String.format("range can not start beyond current length (%d) : %d", getLength(),start));
            }        
            builder.delete((int)range.getStart(), (int)range.getEnd()+1);
        }
        return this;
    }
    /**
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, String) insert(0,sequence)}
     * @param sequence the nucleotide sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, String)
     */
    public NucleotideSequenceBuilder prepend(String sequence){
        return insert(0, sequence);
    }
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder insert(int offset, Iterable<Nucleotide> sequence){
       return insert(offset, Nucleotides.asString(sequence));
    }
    public NucleotideSequenceBuilder insert(int offset, Nucleotide base){
        return insert(offset, base.toString());
     }
    /**
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, Iterable) insert(0,sequence)}
     * @param sequence the nucleotide sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, Iterable)
     */
    public NucleotideSequenceBuilder prepend(Iterable<Nucleotide> sequence){
        return insert(0, Nucleotides.asString(sequence));
    }
    /**
    * Create a new NucleotideSequence instance
    * from the current mutable nucleotides.
    * @return a new NucleotideSequence never null
    * but may be empty.
    */
    @Override
    public NucleotideSequence build() {
        return NucleotideSequenceFactory.create(builder);
    }
    
    
    public String subString(Range range){
        return builder.substring((int)range.getStart(), (int)range.getEnd()+1);
    }

}
