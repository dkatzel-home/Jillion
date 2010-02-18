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
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit.glyph;

import java.util.List;

import org.jcvi.assembly.edit.EditException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;

public abstract class AbstractReplaceGlyphEdit<G extends Glyph,E extends EncodedGlyphs<G>> extends AbstractGlyphEdit<G,E> {

    private final G newBase;
    private final int offset;
    
    /**
     * @param newBase
     * @param offset
     */
    public AbstractReplaceGlyphEdit(G newBase, int offset) {
        this.newBase = newBase;
        this.offset = offset;
    }

    @Override
    public E performEdit(E original) throws EditException {
        List<G> decoded = original.decode();
        decoded.remove(offset);
        decoded.add(offset,newBase);
        return encode(decoded);
    }
    
    
   

}
