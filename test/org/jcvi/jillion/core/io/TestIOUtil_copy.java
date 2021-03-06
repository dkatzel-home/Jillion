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
package org.jcvi.jillion.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static org.junit.Assert.*;

import org.easymock.IAnswer;
import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import static org.easymock.EasyMock.*;
public class TestIOUtil_copy {

	@Test
	public void copySmallFile() throws IOException{
		String inputString = "this is input/blah";
		InputStream inStream = new ByteArrayInputStream(inputString.getBytes(IOUtil.UTF_8));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		long numberOfBytes =IOUtil.copy(inStream, out);
		String actual = new String(out.toByteArray(), IOUtil.UTF_8);
		assertEquals(inputString, actual);
		assertEquals(out.toByteArray().length, numberOfBytes);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullInputStreamShouldThrowNPE() throws IOException{
		IOUtil.copy(null, new ByteArrayOutputStream());
	}
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE() throws IOException{
		String inputString = "this is input/blah";
		InputStream inStream = new ByteArrayInputStream(inputString.getBytes(IOUtil.UTF_8));
		IOUtil.copy(inStream, null);
	}
	
	@Test
	public void copyLargeFile() throws IOException{
		InputStream in = createMock(InputStream.class);
		OutputStream out = createMock(OutputStream.class);
		
		
		out.write(isA(byte[].class), anyInt(), anyInt());
		expectLastCall().anyTimes();
		
		out.flush();
		expectLastCall().anyTimes();
		
		final LargeCopyHelper helper = new LargeCopyHelper();
		
		
		expect(in.read(isA(byte[].class))).andStubAnswer(new IAnswer<Integer>() {

			@Override
			public Integer answer() throws Throwable {
				return helper.read((byte[])getCurrentArguments()[0]);
			}
			
		});
		
		
		replay(in,out);
		long actualNumberOfBytes = IOUtil.copy(in, out);
		assertEquals(LargeCopyHelper.actualNumberOfBytes, actualNumberOfBytes);
	}
	
	private static class LargeCopyHelper{
		
		public static  long actualNumberOfBytes = ((long)Integer.MAX_VALUE)+1;
		
		private long numberOfBytesLeft= actualNumberOfBytes;
		
		
		public int read(byte[] array){
			int arrayLength = array.length;
			if(numberOfBytesLeft > arrayLength){
				numberOfBytesLeft-=arrayLength;
				return arrayLength;
			}
			int returnValue = (int)numberOfBytesLeft;
			//need to return -1 to say we are at EOF
			numberOfBytesLeft=-1;
			return returnValue;
		}
	}
}
