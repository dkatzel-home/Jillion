/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
/*
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.core.Range;

final class DefaultCasMatch implements CasMatch{
    private final boolean hasMatch,
     isPartOfPair;
    private final CasAlignment alignment;
    private final long numberOfMatches,numberOfReportedAlignments;
    private final int score;
    private final Range trimRange;
  
    public DefaultCasMatch(boolean hasMatch, long numberOfMatches,
            long numberOfReportedAlignments,
             boolean isPartOfPair,CasAlignment chosenAlignment, int score) {
       this(hasMatch, numberOfMatches, numberOfMatches, isPartOfPair, chosenAlignment, score, null);
    }
    
 
    public DefaultCasMatch(boolean hasMatch, long numberOfMatches,
            long numberOfReportedAlignments,
             boolean isPartOfPair,CasAlignment chosenAlignment, int score,
             Range trimRange) {
        this.hasMatch = hasMatch;
        this.numberOfMatches = numberOfMatches;
        this.numberOfReportedAlignments = numberOfReportedAlignments;
        this.isPartOfPair = isPartOfPair;
        this.alignment = chosenAlignment;
        this.score = score;
        this.trimRange = trimRange;
    }

    
    @Override
	public Range getTrimRange() {
		return trimRange;
	}


	@Override
    public boolean hasMultipleAlignments() {
        return numberOfReportedAlignments>1;
    }

    @Override
    public boolean matchReported() {
        return hasMatch;
    }

    @Override
    public boolean readHasMutlipleMatches() {
        return numberOfMatches>1;
    }

    @Override
    public boolean readIsPartOfAPair() {
        return isPartOfPair;
    }

    @Override
    public CasAlignment getChosenAlignment() {
        return alignment;
    }

    @Override
    public String toString() {
        StringBuilder result= new StringBuilder("DefaultCasMatch [hasMatch=" + hasMatch
                + ", numberOfAlignments=" + numberOfReportedAlignments
                + ", numberOfMatches=" + numberOfMatches
                + ", isPartOfPair=" + isPartOfPair + ", alignments=");
        if(alignment !=null){
            result.append(String.format("%n%n\t%s", alignment));
        }
               result.append(String.format("%n]"));
        
        return result.toString();
    }

    @Override
    public long getNumberOfReportedAlignments() {
        return numberOfReportedAlignments;
    }

    @Override
    public long getNumberOfMatches() {
        return numberOfMatches;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((alignment == null) ? 0 : alignment.hashCode());
        result = prime * result + (hasMatch ? 1231 : 1237);
        result = prime * result + (isPartOfPair ? 1231 : 1237);
        result = prime * result
                + (int) (numberOfMatches ^ (numberOfMatches >>> 32));
        result = prime * result + score;
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
        if (!(obj instanceof DefaultCasMatch)) {
            return false;
        }
        DefaultCasMatch other = (DefaultCasMatch) obj;
        if (alignment == null) {
            if (other.alignment != null) {
                return false;
            }
        } else if (!alignment.equals(other.alignment)) {
            return false;
        }
        if (hasMatch != other.hasMatch) {
            return false;
        }
        if (isPartOfPair != other.isPartOfPair) {
            return false;
        }
        if (numberOfMatches != other.numberOfMatches) {
            return false;
        }
        if (score != other.score) {
            return false;
        }
        return true;
    }

    
   

  

}
