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
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.data;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRUtil;

/**
 * <code>ZLibData</code> is the {@link Data} implementation
 * that uses the zip algorithm
 * to compress the data. The {@link #encodeData(byte[])}
 * method
 * will encode chromatgoram data but will create about a 5% larger filesize
 * compared to the staden IO_Lib zip implementation.
 * This is probably due to the standard Java implementation of zip does not allow
 * changing the "windowbits" size which could result in better
 * compression.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public enum ZLibData implements Data {
    /**
     * Singleton instance of ZLibData.
     */
    INSTANCE;
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data) throws IOException {
    	Inflater decompresser=null;
        try{
            ByteBuffer size = ByteBuffer.allocate(4);
            size.put(data,1,4);    
            int uncompressedLength =(int) ZTRUtil.readInt(IOUtil.switchEndian(size.array()));            
            ByteBuffer compressedData = ByteBuffer.allocate(data.length-5);
            compressedData.put(data,5,data.length-5);
            //decompress the zipped data
            decompresser = new Inflater();
            decompresser.setInput(compressedData.array());
            ByteBuffer byteBuffer = ByteBuffer.allocate(uncompressedLength);
            decompresser.inflate(byteBuffer.array());
            
            
            return byteBuffer.array();
        }catch(DataFormatException dfEx){
            throw new IOException("could not parse ZLibData",dfEx);
        }finally{
        	//call end in finally block to avoid
        	//memory leak incase of exception thrown.
        	if(decompresser !=null){
        		decompresser.end();
        	}
        }
    }

	@Override
	public byte[] encodeData(byte[] data) throws IOException {
		
		Deflater zlibCompresser= new Deflater();
		try{
			zlibCompresser.setInput(data);
			zlibCompresser.finish();
			byte[] compressedData = new byte[data.length*2];
			int numberOfCompressedBytes =zlibCompresser.deflate(compressedData);
			//5 = header length + uncompressed size as int
			ByteBuffer encodedBuffer = ByteBuffer.allocate(numberOfCompressedBytes+5);
			encodedBuffer.put(DataHeader.ZLIB_ENCODED);
			//for some reason length is different endian...
			byte size[] =IOUtil.switchEndian(IOUtil.convertUnsignedIntToByteArray(data.length));
			encodedBuffer.put(size);
			encodedBuffer.put(compressedData,0, numberOfCompressedBytes);
			encodedBuffer.flip();
			return Arrays.copyOfRange(encodedBuffer.array(), 0, encodedBuffer.limit());
		}finally{
			//always call end() to avoid
			//memory leaks
			zlibCompresser.end();
		}
	}
	/**
	 * This returns the same result as {@link #encodeData(byte[])}
	 * the optional parameter is ignored. 
	 */
	@Override
	public byte[] encodeData(byte[] data, byte ignored)
			throws IOException {
		return encodeData(data);
	}
}
