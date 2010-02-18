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
package org.jcvi.assembly.edit.placed;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.edit.EditException;
import org.jcvi.assembly.edit.glyph.EncodedGlyphEdit;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public abstract class ModifyBaseCallPlacedReadEdit<P extends PlacedRead> extends AbstractPlacedReadEdit<P> {

    private final EncodedGlyphEdit<NucleotideGlyph, NucleotideEncodedGlyphs> basecallEdit;

    /**
     * @param basecallEdit
     */
    public ModifyBaseCallPlacedReadEdit(
            EncodedGlyphEdit<NucleotideGlyph, NucleotideEncodedGlyphs> basecallEdit) {
        this.basecallEdit = basecallEdit;
    }

    @Override
    public P performEdit(P original) throws EditException {
        final NucleotideEncodedGlyphs originalBasecalls = original.getEncodedGlyphs();
        NucleotideEncodedGlyphs editedBasecalls = basecallEdit.performEdit(originalBasecalls);
        Range editedValidRange = editValidRange(original.getValidRange(),originalBasecalls,editedBasecalls);
        
        return createNewPlacedRead(editedBasecalls, 
                original.getStart(), 
                editedValidRange, 
                original.getSequenceDirection());
    }

    protected abstract Range editValidRange(Range originalValidRange,NucleotideEncodedGlyphs original, NucleotideEncodedGlyphs edited);
}
