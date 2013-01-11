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
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZtrChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.phd.SinglePhdFile;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.junit.Test;
public class TestSangerTraceParser {

    private static final String ZTR_FILE = "chromat/ztr/files/GBKAK82TF.ztr";
    private static final String SCF3_FILE = "chromat/scf/files/GBKAK82TF.scf";
    private static final String PHD_FILE = "phd/files/1095595674585.phd.1";
    
    SangerTraceParser sut = SangerTraceParser.INSTANCE;
    private final static ResourceHelper RESOURCES = new ResourceHelper(TestSangerTraceParser.class);
    
    @Test
    public void parseZTR() throws TraceDecoderException, IOException{
        SangerTrace actual =sut.decode("GBKAK82TF.ztr",RESOURCES.getFileAsStream(ZTR_FILE));
        SangerTrace expected = new ZtrChromatogramBuilder("GBKAK82TF.ztr", RESOURCES.getFile(ZTR_FILE)).build();
        assertEquals(expected, actual);
    }
    @Test
    public void parseSCF_v3() throws TraceDecoderException, IOException{
        SangerTrace actual =sut.decode("GBKAK82TF.scf",RESOURCES.getFileAsStream(SCF3_FILE));
        SangerTrace expected = new ScfChromatogramBuilder("GBKAK82TF.scf", RESOURCES.getFile(SCF3_FILE))
									.build();
        assertEquals(expected, actual);
    }
    @Test
    public void parsePhd() throws TraceDecoderException, IOException{
        SangerTrace actual =sut.decode("1095595674585.phd.1",RESOURCES.getFileAsStream(PHD_FILE));
        SangerTrace expected = SinglePhdFile.create(RESOURCES.getFile(PHD_FILE));
        assertEquals(expected, actual);
    }
}
