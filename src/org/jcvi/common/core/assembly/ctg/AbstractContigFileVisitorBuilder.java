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
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ctg;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

public abstract class AbstractContigFileVisitorBuilder extends AbstractContigFileVisitor{

    private DefaultContig.Builder currentContigBuilder;
    
    protected abstract void  addContig(Contig<PlacedRead> contig);

    @Override
    protected void visitRead(String readId, int offset, Range validRange,
            String basecalls, Direction dir) {
       
        currentContigBuilder.addRead(readId, offset, validRange,basecalls,dir,(int)validRange.getEnd()); 
        
        
    }

    @Override
    protected void visitEndOfContig() {
        addContig(currentContigBuilder.build());
    }

    @Override
    protected void visitBeginContig(String contigId, String consensus) {
        currentContigBuilder = new DefaultContig.Builder(contigId,
                encodedConsensus(consensus));
    }
    private NucleotideSequence encodedConsensus(String basecalls) {
        return new NucleotideSequenceBuilder(basecalls).build();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {        
        super.visitEndOfFile();
        currentContigBuilder=null;
    }
    
    
}
