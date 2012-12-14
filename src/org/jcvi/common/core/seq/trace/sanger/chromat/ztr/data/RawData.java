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
/*
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.ztr.data;

import java.nio.ByteBuffer;

import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.TraceEncoderException;



/**
 * This is the implementation of the ZTR Raw Data Format.  This data
 * has no encoding.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public enum RawData implements Data {
    /**
     * Singleton instance of RawData.
     */
    INSTANCE;
    /**
     * Since the given data is not encoded, 
     * return the data back as is.
     * @param data the already completely decoded data
     * @return the same reference to the given data.
     */
    @Override
    public byte[] parseData(byte[] data) throws TraceDecoderException {
        //this is raw data
       return data;
    }
    /**
     * Creates a new array with the first element as {@link DataHeader#RAW}
     * and the rest of the array exactly matches the elements in the given
     * byte array.
     * @param data the raw data to encode.
     * @throws TraceEncoderException if there is a problem encoding the data.
     */
	@Override
	public byte[] encodeData(byte[] data) throws TraceEncoderException {
		ByteBuffer encodedData = ByteBuffer.allocate(data.length+1);
		encodedData.put(DataHeader.RAW);
		encodedData.put(data);
		return encodedData.array();
	}
	/**
	 * This returns the same result as {@link #encodeData(byte[])}
	 * the optional parameter is ignored. 
	 */
	@Override
	public byte[] encodeData(byte[] data, byte ignored)
			throws TraceEncoderException {
		return encodeData(data);
	}

}