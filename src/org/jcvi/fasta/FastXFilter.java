/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
package org.jcvi.fasta;

import org.jcvi.datastore.DataStoreFilter;

/**
 * {@code FastXFilter} filters a FastX (Fasta or Fastq) file.
 * @author dkatzel
 *
 *
 */
public interface FastXFilter extends DataStoreFilter{
    /**
     * filters the fastX record with the given read id and optional comment.
     * @param id the id of the read to possibly filter.
     * @param optionalComment the comment of the read, will be {@code null}
     * if no comment exists.
     * @return {@code true} if the read meets the filter criteria {@code false} otherwise.
     */
    boolean accept(String id, String optionalComment);
    
}
