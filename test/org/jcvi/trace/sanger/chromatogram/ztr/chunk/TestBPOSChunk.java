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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.BPOSChunk;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestBPOSChunk {

    private short[] expected = new short[]{10,20,30,41,53,60,68};
    BPOSChunk sut= new BPOSChunk();

    @Test
    public void valid() throws TraceDecoderException{
        ByteBuffer buf = ByteBuffer.allocate(expected.length*4 + 4);
        buf.putInt(0); //padding
        for(int i=0; i< expected.length; i++){
            buf.putInt(expected[i]);
        }
        ZTRChromatogramBuilder mockStruct = new ZTRChromatogramBuilder();

        sut.parseData(buf.array(), mockStruct);
        assertTrue(Arrays.equals(expected, mockStruct.peaks()));
    }
}
