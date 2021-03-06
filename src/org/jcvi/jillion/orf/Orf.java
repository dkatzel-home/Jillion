/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.orf;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;

public class Orf {

    private final Frame frame;
    private final ProteinSequence seq;
    private final Range range;
    
    public Orf(Frame frame, ProteinSequence seq, Range range) {
        this.frame = frame;
        this.seq = seq;
        this.range = range;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((frame == null) ? 0 : frame.hashCode());
        result = prime * result + ((range == null) ? 0 : range.hashCode());
        result = prime * result + ((seq == null) ? 0 : seq.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Orf)) {
            return false;
        }
        Orf other = (Orf) obj;
        if (frame != other.frame) {
            return false;
        }
        if (range == null) {
            if (other.range != null) {
                return false;
            }
        } else if (!range.equals(other.range)) {
            return false;
        }
        if (seq == null) {
            if (other.seq != null) {
                return false;
            }
        } else if (!seq.equals(other.seq)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Orf [frame=" + frame + ", seq=" + seq + ", range=" + range
                + "]";
    }

    
}
