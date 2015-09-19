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
 * Created on Dec 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr.data;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.jillion.internal.trace.chromat.ztr.data.DeltaEncodedData;
import org.junit.Test;
public class TestDelta8Data {

    private static byte[] uncompressed = new byte[]{10,20,10,(byte)200, (byte)190, 5};
    private static final byte[] COMPRESSED_LEVEL_1;
    private static final byte[] COMPRESSED_LEVEL_2;
    private static final byte[] COMPRESSED_LEVEL_3;
    
    DeltaEncodedData sut = DeltaEncodedData.BYTE;
    
    static{
    	COMPRESSED_LEVEL_1 = createCompressedLevel1();
    	COMPRESSED_LEVEL_2 = createCompressedLevel2();
    	COMPRESSED_LEVEL_3 = createCompressedLevel3();
    }
    
    private static byte[] createCompressedLevel1() {
    	 ShortBuffer compressed = ShortBuffer.allocate(8);
         compressed.put((byte)64);
         compressed.put((short)1);  //level
         int delta=0;
         int prevValue=0;
         for(int i=0; i< uncompressed.length; i++){
             delta = prevValue;
             prevValue = fixSign(uncompressed[i]);
             compressed.put((short)fixSign(prevValue -delta));
         }        
         compressed.flip();
         return convertToByteBuffer(compressed).array();
	}
    private static byte[] createCompressedLevel2() {
    	ShortBuffer compressed = ShortBuffer.allocate(8);
        compressed.put((byte)64);
        compressed.put((short)2);  //level
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        for(int i=0; i< uncompressed.length; i++){
            delta = 2*prevValue -prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = fixSign(uncompressed[i]);
            compressed.put((short)fixSign(prevValue -delta));
        }        
        compressed.flip();
        return convertToByteBuffer(compressed).array();
    }
    
    private static byte[] createCompressedLevel3() {
    	 ShortBuffer compressed = ShortBuffer.allocate(8);
         compressed.put((byte)64);
         compressed.put((short)3);  //level
         int delta=0;
         int prevValue=0;
         int prevPrevValue=0;
         int prevPrevPrevValue =0;
         for(int i=0; i< uncompressed.length; i++){
             delta = 3*prevValue - 3*prevPrevValue + prevPrevPrevValue;
             prevPrevPrevValue= prevPrevValue;
             prevPrevValue= prevValue;
             prevValue = fixSign(uncompressed[i]);
             compressed.put((short)fixSign(prevValue -delta));
         }        
         compressed.flip();
         return convertToByteBuffer(compressed).array();
    }
    @Test
    public void level1(){
        byte[] actual = sut.parseData(COMPRESSED_LEVEL_1);
        assertArrayEquals(actual, uncompressed);
    }
    @Test
    public void compressedLevel1() throws IOException{
    	byte[] actual = sut.encodeData(uncompressed, DeltaEncodedData.Level.DELTA_LEVEL_1);
    	assertArrayEquals(COMPRESSED_LEVEL_1, actual);
    }
    
	@Test
    public void level2(){
        byte[] actual = sut.parseData(COMPRESSED_LEVEL_2);
        assertArrayEquals(actual, uncompressed);
    }
	@Test
    public void compressedLevel2() throws IOException{
    	byte[] actual = sut.encodeData(uncompressed, DeltaEncodedData.Level.DELTA_LEVEL_2);
    	assertArrayEquals(COMPRESSED_LEVEL_2, actual);
    }
    
    @Test
    public void level3(){
      byte[] actual = sut.parseData(COMPRESSED_LEVEL_3);
      assertArrayEquals(actual, uncompressed);
    }
    @Test
    public void compressedLevel3() throws IOException{
    	byte[] actual = sut.encodeData(uncompressed, DeltaEncodedData.Level.DELTA_LEVEL_3);
    	assertArrayEquals(COMPRESSED_LEVEL_3, actual);
    }
    private static ByteBuffer convertToByteBuffer(ShortBuffer compressed) {
        ByteBuffer toBytes = ByteBuffer.allocate(8);
        while(compressed.hasRemaining()){
            toBytes.put((byte)compressed.get());
        }
        return toBytes;
    }
    private static int fixSign(int prevValue) {
        if(prevValue<0){
            prevValue +=256;
        }
        if(prevValue >255){
            prevValue -= 256;
        }
        return prevValue;
    }
}
