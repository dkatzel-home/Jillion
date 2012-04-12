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
import java.math.BigInteger;
import java.util.Arrays;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

enum DefaultSFFCommonHeaderDecoder implements SffCommonHeaderDecoder {
	/**
	 * Singleton instance.
	 */
	INSTANCE
	;
    private static final byte[] MAGIC_NUMBER = ".sff".getBytes(IOUtil.UTF_8);
    private static final byte[] ACCEPTED_VERSION = new byte[]{0,0,0,1};
    /**
     * Currently SFF only has 1 format code which has a value of <code>1</code>.
     */
    private static final byte FORMAT_CODE = 1;
    @Override
    public SffCommonHeader decodeHeader(DataInputStream in) throws SffDecoderException{

        try{
            verifyMagicNumber(in);
            verifyVersion1(in);
            BigInteger indexOffset = IOUtil.readUnsignedLong(in);
            long indexLength = IOUtil.readUnsignedInt(in);
            long numReads = IOUtil.readUnsignedInt(in);
            //skip header length
            IOUtil.readUnsignedShort(in);
            int keyLength = IOUtil.readUnsignedShort(in);
            int flowsPerRead = IOUtil.readUnsignedShort(in);
            verifyFlowgramFormatCode(in);
            NucleotideSequence flow = readFlow(in,flowsPerRead);
            NucleotideSequence keySequence = readKeySequence(in, keyLength);
            int bytesReadSoFar = 31+flowsPerRead+keyLength;
            int padding =SffUtil.caclulatePaddedBytes(bytesReadSoFar);
            IOUtil.blockingSkip(in, padding);

            return new DefaultSffCommonHeader(indexOffset, indexLength,
            numReads, flowsPerRead, flow,
            keySequence);

        }
        catch(IOException e){
            throw new SffDecoderException("error decoding sff file",e);
        }

    }
    private NucleotideSequence readFlow(DataInputStream in, int flowsPerRead) throws IOException, SffDecoderException {
        byte[] flow = new byte[flowsPerRead];
        try{
        	IOUtil.blockingRead(in, flow);
        }catch(IOException e){
        	throw new SffDecoderException("error decoding flow",e);
        }
        return new NucleotideSequenceBuilder(new String(flow,IOUtil.UTF_8))
        		.build();
    }
    private NucleotideSequence readKeySequence(DataInputStream in, int keyLength) throws IOException, SffDecoderException {
        byte[] keySequence = new byte[keyLength];
        try{
        	IOUtil.blockingRead(in, keySequence);
        }catch(IOException e){
        	throw new SffDecoderException("error decoding keySequence",e);
        }       
        return new NucleotideSequenceBuilder(new String(keySequence,IOUtil.UTF_8))
        			.build();
    }
    private void verifyFlowgramFormatCode(DataInputStream in) throws IOException, SffDecoderException {
        //currently only 1 format code
        if(in.readByte() != FORMAT_CODE){
            throw new SffDecoderException("unknown flowgram format code");
        }

    }
    private void verifyVersion1(DataInputStream in) throws IOException, SffDecoderException {
        byte[] versionArray = new byte[4];
        IOUtil.blockingRead(in, versionArray);
        if(!Arrays.equals(versionArray, ACCEPTED_VERSION)){
            throw new SffDecoderException("version not compatible with decoder");
        }

    }
    private void verifyMagicNumber(DataInputStream in) throws IOException,
            SffDecoderException {
        byte[] actualMagicNumber = new byte[4];
        IOUtil.blockingRead(in, actualMagicNumber);
        if(!Arrays.equals(actualMagicNumber, MAGIC_NUMBER)){
            throw new SffDecoderException("magic number does not match expected");
        }
    }
}
