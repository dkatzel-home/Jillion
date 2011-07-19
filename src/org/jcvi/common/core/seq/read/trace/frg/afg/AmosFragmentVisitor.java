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

package org.jcvi.common.core.seq.read.trace.frg.afg;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.io.TextFileVisitor;

/**
 * @author dkatzel
 *
 *
 */
public interface AmosFragmentVisitor extends TextFileVisitor{

    boolean visitRead(int index, String id);
    
    void visitBasecalls(NucleotideSequence basecalls);
    
    void visitQualities(List<PhredQuality> qualities);
    
    void visitClearRange(Range clearRange);
    
    void visitVectorRange(Range vectorClearRange);
    
    void visitQualityRange(Range qualityClearRange);
}
