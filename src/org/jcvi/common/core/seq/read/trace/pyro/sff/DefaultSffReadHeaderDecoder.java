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
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.DataInputStream;
import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.io.IOUtil;

enum DefaultSffReadHeaderDecoder implements SffReadHeaderDecoder {
	/**
	 * Singleton instance.
	 */
	INSTANCE;
	
    @Override
    public SffReadHeader decodeReadHeader(DataInputStream in)
            throws SffDecoderException {
        try{
            short headerLength =in.readShort();
            short nameLegnth = in.readShort();
            int numBases = in.readInt();
            short qualLeft = in.readShort();
            short qualRight = in.readShort();
            short adapterLeft = in.readShort();
            short adapterRight = in.readShort();
            String name = readSequenceName(in,nameLegnth);
            int bytesReadSoFar = 16+nameLegnth;
            int padding =SffUtil.caclulatePaddedBytes(bytesReadSoFar);
            if(headerLength != bytesReadSoFar+padding){
                throw new SffDecoderException("invalid header length");
            }
            IOUtil.blockingSkip(in, padding);
            
            return new DefaultSffReadHeader(numBases,
                    Range.of(CoordinateSystem.RESIDUE_BASED, qualLeft, qualRight),
                    Range.of(CoordinateSystem.RESIDUE_BASED, adapterLeft, adapterRight),
                     name);
        }
        catch(IOException e){
            throw new SffDecoderException("error trying to decode read header",e);
        }
    }


    private String readSequenceName(DataInputStream in, short length) throws IOException {
        byte[] name = new byte[length];
        try{
        	IOUtil.blockingRead(in, name);
        }catch(IOException e){
        	throw new SffDecoderException("error decoding seq name",e);
        }
       
        return new String(name,IOUtil.UTF_8);
    }

    
}
