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
 * Created on May 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.jcvi.glyph.qualClass.QualityClass;

public class QualityClassContigMapXMLWriter {

    private static final String QUALITY_CLASS_REGION_FORMAT = 
        "<coverageregion start =\"%d\" end=\"%d\" value= \"%d\"/>\n";

    public void write(OutputStream out, List<QualityClassRegion> qualityClassRegions) throws IOException{
        out.write("<qualityclassmap>\n".getBytes());
        for(QualityClassRegion region : qualityClassRegions){
            final QualityClass qualityClass = region.getQualityClass();
            out.write(String.format(QUALITY_CLASS_REGION_FORMAT, region.getStart(), region.getEnd(), 
                    qualityClass.getValue()).getBytes());
        }
        out.write("</qualityclassmap>\n".getBytes());
        out.flush();
    }
}
