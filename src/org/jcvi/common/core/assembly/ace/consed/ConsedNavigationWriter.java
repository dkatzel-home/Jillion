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

package org.jcvi.common.core.assembly.ace.consed;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;

/**
 * {@code ConsedNavigationWriter} write Consed
 * Custom Navigation Files to allow consed
 * users to quickly jump to feature locations
 * while editing.
 * @author dkatzel
 *
 *
 */
public final class ConsedNavigationWriter implements Closeable{

    private final OutputStream out;
    
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    
    public static ConsedNavigationWriter create(String title, OutputStream out) throws IOException{
        if(title ==null){
            throw new NullPointerException("title can not be null");
        }
        return new ConsedNavigationWriter(title, out);
    }
    /**
     * Create a partial consed navigiation file without a title.
     * This should only be used if the navigation file creation is being split up 
     * into multiple pieces to be combined downstream.
     * @param out
     * @return
     * @throws IOException
     */
    public static ConsedNavigationWriter createPartial(OutputStream out) throws IOException{
        return new ConsedNavigationWriter(null, out);
    }
    /**
     * Creates a new ConsedNavigationWriter which will
     * write out navigation data to the given {@link OutputStream}.
     * @param title the title of this navigation file.
     * @param out the OutputStream to write to.
     * @throws IOException if there is a problem writing
     * to the outputStream.
     * @throws NullPointerException if title or outputStream are {@code null}.
     */
    private ConsedNavigationWriter(String title, OutputStream out) throws IOException{
        if(out ==null){
            throw new NullPointerException("output stream can not be null");
        }
        if(title!=null){
            out.write(String.format("TITLE: %s%n",title).getBytes(UTF_8));
        }
        
        this.out= out;
        
    }
    @Override
    public void close() throws IOException {
        out.close();
    }
    
    public void writeNavigationElement(ReadNavigationElement element) throws IOException{
        
        StringBuilder builder = new StringBuilder("BEGIN_REGION\n");
        builder.append(String.format("TYPE: %s%n",element.getType()));
        builder.append(String.format("READ: %s%n",element.getTargetId()));
        Range range = element.getUngappedPositionRange();
        builder.append(String.format("UNPADDED_READ_POS: %d %d%n",range.getBegin(CoordinateSystem.RESIDUE_BASED), range.getEnd(CoordinateSystem.RESIDUE_BASED)));
        String comment = element.getComment();
        //consed requires a comment line even if it is empty
        builder.append(String.format("COMMENT: %s%n",comment==null? "": comment));
        builder.append("END_REGION\n");
        out.write(builder.toString().getBytes(UTF_8));
    }
    
    public void writeNavigationElement(ConsensusNavigationElement element) throws IOException{
        
        StringBuilder builder = new StringBuilder("BEGIN_REGION\n");
        builder.append(String.format("TYPE: %s%n",element.getType()));
        builder.append(String.format("CONTIG: %s%n",element.getTargetId()));
        Range range = element.getUngappedPositionRange();
        builder.append(String.format("UNPADDED_CONS_POS: %d %d%n",range.getBegin(CoordinateSystem.RESIDUE_BASED), range.getEnd(CoordinateSystem.RESIDUE_BASED)));
        String comment = element.getComment();
        //consed requires a comment line even if it is empty
        builder.append(String.format("COMMENT: %s%n",comment==null? "": comment));
       
        builder.append("END_REGION\n");
        
        out.write(builder.toString().getBytes(UTF_8));
    }
}
