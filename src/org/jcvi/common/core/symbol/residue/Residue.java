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

package org.jcvi.common.core.symbol.residue;

import org.jcvi.common.core.symbol.Symbol;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;

/**
 * @author dkatzel
 *
 *
 */
public interface Residue extends Symbol{

	byte getOrdinalAsByte();
	
	 /**
     * Return the Character equivalent of this
     * {@link Nucleotide}.  For example
     * calling this method for {@link #Adenine}
     * will return 'A'.
     * @return the Character equivalent of this.
     */
    public Character getCharacter();
}
