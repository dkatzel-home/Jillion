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
package org.jcvi.jillion.core.residue.nt;


import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
public class TestNucleotideSequenceMatches {

    @Test
    public void noMatchesReturnsEmptyStream(){
        NucleotideSequence sut = NucleotideSequenceTestUtil.create("AAA", 10);
        
        assertEquals(0L, sut.findMatches("G").count());
    }
    
    @Test
    public void exactMatch(){
        NucleotideSequence sut = NucleotideSequence.of("ACGTACGT");
        
        assertEquals(Arrays.asList(Range.ofLength(8)),  sut.findMatches("ACGTACGT").collect(Collectors.toList()));
    }
    @Test
    public void braceMatching(){
        NucleotideSequence sut = NucleotideSequence.of("ACGTACGT");
        
        assertEquals(Arrays.asList(Range.ofLength(8)),  sut.findMatches("(ACGT){2}").collect(Collectors.toList()));
    }
    @Test
    public void multipleMatches(){
        NucleotideSequence sut = NucleotideSequence.of("ACGTACGT");
        
        assertEquals(Arrays.asList(Range.of(0,3), Range.of(4,7)),  sut.findMatches("ACGT").collect(Collectors.toList()));
    }
    
    @Test
    public void multipleMatches2(){
        NucleotideSequence sut = NucleotideSequence.of("ATTACCGTTA");
        
        assertEquals(Arrays.asList(Range.of(0,3), Range.of(6,9)),  sut.findMatches(".TTA").collect(Collectors.toList()));
    }
    @Test
    public void wildcard(){
        NucleotideSequence sut = NucleotideSequence.of("ATTACCGTTA");
        
        assertEquals(Arrays.asList(Range.of(4,9)),  sut.findMatches("CCG.+").collect(Collectors.toList()));
    }
    
    @Test
    public void exactChars(){
        NucleotideSequence sut = NucleotideSequence.of("ATTACCGTTA");
        
        assertEquals(Arrays.asList(Range.of(0,3), Range.of(6,9)),  
                sut.findMatches("[AG]TTA").collect(Collectors.toList()));
    }
    
    @Test
    public void exactCharSubRange(){
        NucleotideSequence sut = NucleotideSequence.of("ATTACCGTTA");
        
        assertEquals(Arrays.asList(/*Range.of(0,3), */Range.of(6,9)), 
                
                sut.findMatches("[AG]TTA", Range.of(4,9))
                .collect(Collectors.toList()));
    }
    
    @Test
    public void subRangeSearchIsSameAsFilteringFullRangeSearch(){
        NucleotideSequence sut = NucleotideSequence.of("ATTACCGTTA");
        Pattern pattern = Pattern.compile("[AG]TTA");
        Range subRange = Range.of(4,9);
        
        List<Range> filteredRange = sut.findMatches(pattern)
                                        .filter(r-> r.isSubRangeOf(subRange))
                                        .collect(Collectors.toList());
        
       
        assertEquals(filteredRange, sut.findMatches(pattern, subRange) .collect(Collectors.toList()));
    }
    
    @Test
    public void sameAsRangesOfNs(){
        NucleotideSequence sut = NucleotideSequence.of("ACGTACGTNNNACGTACGTNNNNAAAAAAANNNNNNNAAAAAAANNNNN");
        List<Range> expected = sut.getRangesOfNs();
        
        assertEquals(expected, sut.findMatches("[N]+").collect(Collectors.toList()));
    }
}
