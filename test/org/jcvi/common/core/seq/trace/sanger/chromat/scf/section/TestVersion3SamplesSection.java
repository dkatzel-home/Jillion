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
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.scf.section;

import java.nio.ByteBuffer;

import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.SCFUtils;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.section.AbstractSampleSectionCodec;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.section.Version3SampleSectionCodec;

public class TestVersion3SamplesSection  extends AbstractTestSamplesSection{
    @Override
    protected AbstractSampleSectionCodec createSectionHandler() {
        return new Version3SampleSectionCodec();
    }

    @Override
    protected byte[] encodeBytePositions() {
        ByteBuffer result = ByteBuffer.allocate(aSamplesAsBytes.length*4);
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(aSamplesAsBytes));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(cSamples));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(gSamples));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(tSamples));
        return result.array();
    }

    @Override
    protected byte[] encodeShortPositions() {
        ByteBuffer result = ByteBuffer.allocate(aSamplesAsShorts.length*4*2);
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(aSamplesAsShorts));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(cSamples));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(gSamples));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(tSamples));

        return result.array();
    }



    private void bulkShortPut(ByteBuffer buffer, short[] array){
        for(int i=0; i<array.length; i++){
            buffer.putShort(array[i]);
        }
    }

    private void bulkBytePut(ByteBuffer buffer, short[] arrayOfBytes){
        for(int i=0; i<arrayOfBytes.length; i++){
            buffer.put((byte)arrayOfBytes[i]);
        }
    }
}
