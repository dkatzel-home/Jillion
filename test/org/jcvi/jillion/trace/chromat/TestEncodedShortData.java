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
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.trace.chromat.EncodedShortData;
import org.junit.Test;

public class TestEncodedShortData {
    private short[] data = new short[]{100,200,300,400,500,800};
    EncodedShortData sut = new EncodedShortData(data);
    @Test
    public void arrayConstructor(){
        assertTrue(Arrays.equals(data, sut.getData()));
    }
    @Test
    public void nullArrayConstrcutorShouldThrowIllegalArgumentException(){
        try{
           new EncodedShortData((short[])null);
           fail("should throw illegal argument excpetion when constructor passed null");
        }
        catch(IllegalArgumentException expected){
            assertEquals("data can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullStringConstrcutorShouldThrowIllegalArgumentException(){
        try{
           new EncodedShortData((String)null);
           fail("should throw illegal argument excpetion when constructor passed null");
        }
        catch(IllegalArgumentException expected){
            assertEquals("data can not be null", expected.getMessage());
        }
    }
    @Test
    public void invalidEncodedDataShouldThrowNumberFormatException(){
        try{
            new EncodedShortData("not a list of bytes");
            fail("should throw NumberFormatException when can't parse out byte values");
         }
         catch(NumberFormatException expected){
         }
    }
    @Test
    public void StringConstructor(){
        EncodedShortData encoded = new EncodedShortData(encodeData(data));
        assertArrayEquals(data, encoded.getData());
    }
    @Test
    public void StringWithNoWhiteSpaceConstructor(){
        final String noWhiteSpace = encodeData(data).replaceAll(", ", ",");
        EncodedShortData sut = new EncodedShortData(noWhiteSpace);
        assertArrayEquals(data, sut.getData());
    }
    @Test
    public void StringExtraNoWhiteSpaceConstructor(){
        final String extraWhiteSpace = encodeData(data).replaceAll(", ", ",\t  \t");
        EncodedShortData sut = new EncodedShortData(extraWhiteSpace);
        assertArrayEquals(data, sut.getData());
    }
    @Test
    public void StringCarriageReturnsConstructor(){
        final String withCarriageReturns = encodeData(data).replaceAll(", ", ",\n");
        EncodedShortData sut = new EncodedShortData(withCarriageReturns);
        assertArrayEquals(data, sut.getData());
    }

    @Test
    public void encode(){
        String expectedEncodedData = encodeData(data);
        EncodedShortData sut = new EncodedShortData(data);
        assertEquals(expectedEncodedData, sut.encodeData());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void notEqualsNotEncodedData(){
        assertFalse(sut.equals("not EncodedData"));
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }

    @Test
    public void equalsSameValues(){
        EncodedShortData sameValues = new EncodedShortData(data);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentData(){
        short[] differentData =Arrays.copyOf(data, data.length-1);
        EncodedShortData differentValues = new EncodedShortData(differentData);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    private String encodeData(short[] array){
        StringBuilder result = new StringBuilder();
        for(int i=0 ; i < array.length-1; i++){
            result.append(array[i]);
            result.append(", ");
        }
        result.append(array[array.length-1]);
        return result.toString();
    }
}
