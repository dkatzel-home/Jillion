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

package org.jcvi.trace.sanger.chromatogram;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.abi.Ab1FileParser;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramFile;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogram;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramFile;
import org.junit.Test;

public class TestChromatogramFileParser {

	private static final String ZTR_FILE = "ztr/files/GBKAK82TF.ztr";
    private static final String SCF3_FILE = "scf/files/GBKAK82TF.scf";
    private static final String AB1_FILE = "abi/files/SDBHD01T00PB1A1672F.ab1";
    
    
    private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestChromatogramFileParser.class);
    
    @Test
    public void parseZTR() throws TraceDecoderException, IOException{    	
        File ztrFile = RESOURCES.getFile(ZTR_FILE);
        ZTRChromatogram expected = ZTRChromatogramFile.create(ztrFile);
        ZTRChromatogramFile actual = ZTRChromatogramFile.createUnset();
        
		ChromatogramParser.parse(ztrFile,actual);
        assertEquals(expected, actual);
    }
    @Test
    public void parseSCF3() throws TraceDecoderException, IOException{    	
        File scfFile = RESOURCES.getFile(SCF3_FILE);
        SCFChromatogram expected = SCFChromatogramFile.create(scfFile);
        SCFChromatogramFile actual = SCFChromatogramFile.createUnset();
        
		ChromatogramParser.parse(scfFile,actual);
        assertEquals(expected, actual);
    }
    @Test
    public void parseAB1() throws TraceDecoderException, IOException{    	
        File ab1File = RESOURCES.getFile(AB1_FILE);
        BasicChromatogramFile expected = new BasicChromatogramFile();
        Ab1FileParser.parseAb1File(ab1File, expected);
        BasicChromatogramFile actual = new BasicChromatogramFile();
        
		ChromatogramParser.parse(ab1File,actual);
        assertEquals(expected, actual);
    }
}
