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
 * Created on Jan 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestDefaultCoverageRegion {

    Range seq1 = createMock(Range.class);
    Range seq2 = createMock(Range.class);
    int start = 100;
    int length = 200;
    int end = start+length-1;
    DefaultCoverageRegion<Range> sut = new DefaultCoverageRegion.Builder<Range>(start,Arrays.asList(seq1,seq2))
                                        .end(end)
                                        .build();
    Range range = Range.of(start, end);
    @Test
    public void builder(){

        assertEquals(range, sut.asRange());
        assertEquals(Arrays.asList(seq1,seq2), getElements(sut));
        assertEquals(2, sut.getCoverageDepth());
    }
    @Test
    public void add(){

        DefaultCoverageRegion<Range> region = new DefaultCoverageRegion.Builder<Range>(start,Arrays.asList(seq1))
                                    .add(seq2)
                                    .end(end)
                                        .build();
        assertEquals(range, region.asRange());
        assertEquals(Arrays.asList(seq1,seq2), getElements(region));
        assertEquals(2, region.getCoverageDepth());
    }
    
    private List<Range> getElements(CoverageRegion<Range> region){
        List<Range> actual = new ArrayList<Range>();
        for(Range p : region){
            actual.add(p);
        }
        return actual;
    }
    @Test
    public void remove(){
        DefaultCoverageRegion<Range> region = new DefaultCoverageRegion.Builder<Range>(start,Arrays.asList(seq1,seq2))
                    .end(end)
                    .remove(seq2)
                    .build();
        
        assertEquals(range, region.asRange());
        assertEquals(Arrays.asList(seq1), getElements(region));
        assertEquals(1, region.getCoverageDepth());
    }
    
    @Test
    public void endNotSetShouldThowIllegalStateException(){
        
        try{
            new DefaultCoverageRegion.Builder<Range>(start,Arrays.asList(seq1,seq2)).build();
            fail("calling build() before end() should throw illegalState Exception");
        }
        catch(IllegalStateException e){
            assertEquals("end must be set", e.getMessage());
        }
    }
    
    @Test
    public void nullElementsShouldThrowIllegalArgumentException(){
        try{
            new DefaultCoverageRegion.Builder<Range>(start,(Iterable<Range>)null);
            fail("passing in null elements should throw illegalArgumentException");
        }catch(IllegalArgumentException e){
            assertEquals("elements can not be null" , e.getMessage());
        }
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void equalsSameRValues(){
        DefaultCoverageRegion<Range> sameValues = new DefaultCoverageRegion.Builder<Range>(start,Arrays.asList(seq1,seq2))
                                        .end(end)
                                        .build();
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a coverageRegion"));
    }
    
    @Test
    public void notEqualsDifferentStart(){
        DefaultCoverageRegion<Range> differentStart = new DefaultCoverageRegion.Builder<Range>(
                                    start+1,
                                    Arrays.asList(seq1,seq2))
                                        .end(end+1)
                                        .build();
        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentStart);
    }
    
    @Test
    public void notEqualsDifferentLength(){
        DefaultCoverageRegion<Range> differentLength = new DefaultCoverageRegion.Builder<Range>(
                                    start,
                                    Arrays.asList(seq1,seq2))
                                        .end(end+1)
                                        .build();
        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentLength);
    }
    @Test
    public void notEqualsDifferentElements(){
        DefaultCoverageRegion<Range> differentElements = new DefaultCoverageRegion.Builder<Range>(
                                    start,
                                    Arrays.asList(seq1))
                                        .end(end)
                                        .build();
        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentElements);
    }
    
    @Test
    public void sameElementsDifferentOrderAreEqual(){
        DefaultCoverageRegion<Range> differentElementOrder = new DefaultCoverageRegion.Builder<Range>(
                                    start,
                                    Arrays.asList(seq2,seq1))
                                        .end(end)
                                        .build();
        
        TestUtil.assertEqualAndHashcodeSame(sut, differentElementOrder);
    }
    
}
