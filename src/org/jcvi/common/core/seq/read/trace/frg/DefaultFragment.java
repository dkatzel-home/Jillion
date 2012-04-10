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
 * Created on Mar 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.frg;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.trace.Trace;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.CommonUtil;

public class DefaultFragment implements Fragment{
    private final String id;
    private final NucleotideSequence bases;
    private final QualitySequence qualities;
    private final Range validRange, vectorClearRange;
    private final String comment;
    private final Library library;
    public DefaultFragment(String id, Trace trace,Range validRange,Range vectorClearRange, Library library,String comment){
        this(id, trace.getBasecalls(), trace.getQualities(),validRange,vectorClearRange,library,comment);
    }
    public DefaultFragment(String id, Trace trace,Range validRange,Range vectorClearRange, Library library){
        this(id, trace,validRange,vectorClearRange,library,null);
    }
    public DefaultFragment(String id, Trace trace,Range validRange,Library library){
        this(id, trace,validRange,validRange,library,null);
    }
    public DefaultFragment(String id, Trace trace,Library library){
        this(id, trace,Range.createOfLength(0,trace.getBasecalls().getLength()),library);
    }
    public DefaultFragment(String id, NucleotideSequence bases,
            QualitySequence qualities,Range validRange,Range vectorClearRange, Library library,String comment){
        if(id ==null){
            throw new IllegalArgumentException("id can not be null");
        }
        this.id = id;
        this.validRange = validRange;
        this.bases = bases;
        this.qualities = qualities;
        this.comment = comment;
        this.library = library;
        this.vectorClearRange = vectorClearRange;
    }


    @Override
    public NucleotideSequence getBasecalls() {
        return bases;
    }

    @Override
    public QualitySequence getQualities() {
        return qualities;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public Range getValidRange() {
        return validRange;
    }


    @Override
    public NucleotideSequence getNucleotideSequence() {
        return bases;
    }


    @Override
    public long getLength() {
        return bases.getLength();
    }
    public String getComment() {
        return comment;
    }
    @Override
    public Library getLibrary() {
        return library;
    }
    @Override
    public Range getVectorClearRange() {
        return vectorClearRange;
    }
    @Override
    public String getLibraryId() {
        return library.getId();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultFragment)){
            return false;
        }
        DefaultFragment other = (DefaultFragment) obj;
        return CommonUtil.similarTo(getId(), other.getId());
    }

    
}
