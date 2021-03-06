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
package org.jcvi.jillion.align.exonerate.vulgar;

import java.util.Objects;

public class VulgarElement {

    private final VulgarOperation op;
    private final int queryLength;
    private final int targetLength;
    
    
    public VulgarElement(VulgarOperation op, int queryLength,
            int targetLength) {
        this.op = Objects.requireNonNull(op);
        if(queryLength < 0){
            throw new IllegalArgumentException("query length can not be negative : " + queryLength);
        }
        if(targetLength < 0){
            throw new IllegalArgumentException("target length can not be negative : " + targetLength);
        }
        this.queryLength = queryLength;
        this.targetLength = targetLength;
    }
    public VulgarOperation getOp() {
        return op;
    }
    public int getQueryLength() {
        return queryLength;
    }
    public int getTargetLength() {
        return targetLength;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((op == null) ? 0 : op.hashCode());
        result = prime * result + queryLength;
        result = prime * result + targetLength;
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
        if (!(obj instanceof VulgarElement)) {
            return false;
        }
        VulgarElement other = (VulgarElement) obj;
        if (op != other.op) {
            return false;
        }
        if (queryLength != other.queryLength) {
            return false;
        }
        if (targetLength != other.targetLength) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "VulgarElement [op=" + op + ", queryLength=" + queryLength
                + ", targetLength=" + targetLength + "]";
    }
    
    
}
