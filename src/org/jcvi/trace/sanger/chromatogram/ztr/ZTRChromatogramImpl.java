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
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr;

import org.jcvi.Range;
import org.jcvi.trace.sanger.chromatogram.BasicChromatogram;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;

public class ZTRChromatogramImpl extends BasicChromatogram implements ZTRChromatogram{

    /**
     * Hints for valid range of this sequence.
     */
    private Range clip;

    /**
     * Comment on this chromatogram, may be null.
     */
    private String comment;
    /**
     * Constructor that takes a Chromatogram without
     * ztr specific paramters.
     * @param c
     */
    public ZTRChromatogramImpl(Chromatogram c){
        this(c, null, null);
    }
    /**
     * @param c
     */
    public ZTRChromatogramImpl(Chromatogram c, Range clip, String comment) {
        super(c);
        this.clip = clip;
        this.comment = comment;
    }
    /**
     * Gets the ZTR Specific comment.
     * @return a comment, may be null.
     */
    public String getComment() {
        return comment;
    }
    /**
     * Gets the ZTR Specific clip.
     * @return a clip, may be null.
     */
    public Range getClip() {
        return clip;
    }

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        //keep superclass'es equals
        return super.equals(obj);
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        //keep superclass hascode
        return super.hashCode();
    }
    
    
}
