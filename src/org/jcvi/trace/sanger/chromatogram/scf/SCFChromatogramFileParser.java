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

package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.scf.header.DefaultSCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeaderCodec;

/**
 * @author dkatzel
 *
 *
 */
public class SCFChromatogramFileParser {

    private static final SCFHeaderCodec HEADER_CODEC =new DefaultSCFHeaderCodec();
    private static final SCFCodec VERSION3 =new Version3SCFCodec();
    private static final SCFCodec VERSION2 =new Version2SCFCodec();
    
    
    
    public static void parseSCFFile(File scfFile, ChromatogramFileVisitor visitor) throws SCFDecoderException, IOException{
        DataInputStream in=null;
        try{
            in = new DataInputStream(new FileInputStream(scfFile));
             SCFHeader header =HEADER_CODEC.decode(in);
             if(header.getVersion()==3F){
                 VERSION3.parse(scfFile, visitor);
             }else{
                 VERSION2.parse(scfFile, visitor);
             }
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        
    }
}
