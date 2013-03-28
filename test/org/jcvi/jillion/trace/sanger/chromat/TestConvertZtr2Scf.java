/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.chromat;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.SCFChromatogramImpl;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.SCFCodec;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.SCFCodecs;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.sanger.chromat.Chromatogram;
import org.jcvi.jillion.trace.sanger.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
public class TestConvertZtr2Scf {
    private static final ResourceHelper RESOURCES = new ResourceHelper(TestConvertZtr2Scf.class);
    SCFCodec scfCodec = SCFCodecs.VERSION_3;
    
    @Test
    public void ztr2scf() throws TraceDecoderException, IOException{
        
        Chromatogram decodedZTR = new ZtrChromatogramBuilder("GBKAK82TF.ztr", RESOURCES.getFile("ztr/files/GBKAK82TF.ztr"))
        											.build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scfCodec.write(new ScfChromatogramBuilder(decodedZTR).build(), out);

        
        Chromatogram encodedScf = new ScfChromatogramBuilder("id", new ByteArrayInputStream(out.toByteArray()))
								.build();

        assertEquals(decodedZTR, encodedScf);
    }
    
    @Test
    public void scfequalsZtr() throws TraceDecoderException, IOException{
        Chromatogram decodedScf = new ScfChromatogramBuilder("id", RESOURCES.getFile("scf/files/GBKAK82TF.scf"))
        							.build();
        Chromatogram decodedZTR = new ZtrChromatogramBuilder("GBKAK82TF.ztr", RESOURCES.getFile("ztr/files/GBKAK82TF.ztr"))
											.build();
        assertEquals(decodedZTR, decodedScf);        
    }
    /**
     * ZTR files can have no qualities (ex: trash data)
     * but SCF requires the same # of qualities as basecalls
     * so just set them to 0.
     * @throws IOException 
     * @throws TraceDecoderException 
     */
    @Test
    public void ztrWithNoQualitiesShouldGetPaddedQualitiesInScf() throws TraceDecoderException, IOException{
        Chromatogram ztr = new ZtrChromatogramBuilder("GBKAK82TF.ztr", RESOURCES.getFile("ztr/files/515866_G07_AFIXF40TS_026.ab1.afg.trash.ztr"))
												.build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scfCodec.write(new SCFChromatogramImpl(ztr), out);
        
        Chromatogram encodedScf = new ScfChromatogramBuilder("id", new ByteArrayInputStream(out.toByteArray()))
										.build();
        
        int numberOfBases = (int)encodedScf.getNucleotideSequence().getLength();
        QualitySequence expectedQualities = new QualitySequenceBuilder(new byte[numberOfBases]).build();
        
        assertEquals(expectedQualities,encodedScf.getQualitySequence());
    }
    
    
}
