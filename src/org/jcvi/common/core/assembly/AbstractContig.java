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
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;

public abstract class AbstractContig<T extends AssembledRead> implements Contig<T>{
    private NucleotideSequence consensus;
    private String id;
    private Map<String, T> mapById;
    private final int numberOfReads;
    protected AbstractContig(String id, NucleotideSequence consensus, Set<T> placedReads){
        this.id = id;
        this.consensus = consensus;
        this.numberOfReads = placedReads.size();
        mapById = new LinkedHashMap<String, T>(numberOfReads+1, 1F);
        for(T r : placedReads){
            mapById.put(r.getId(), r);
        }
       
    }

    @Override
    public NucleotideSequence getConsensus() {
        return consensus;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getNumberOfReads() {
        return numberOfReads;
    }
    @Override
    public T getRead(String id) {
        return mapById.get(id);
    }
    @Override
    public CloseableIterator<T> getReadIterator() {       
        return CloseableIteratorAdapter.adapt(mapById.values().iterator());
    }

    


    @Override
    public boolean containsRead(String placedReadId) {
        return mapById.containsKey(placedReadId);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((consensus == null) ? 0 : consensus.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + numberOfReads;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Contig)) {
			return false;
		}
		Contig other = (Contig) obj;
		
		if (consensus == null) {
			if (other.getConsensus()!= null) {
				return false;
			}
		} else if (!consensus.equals(other.getConsensus())) {
			return false;
		}
		if (id == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!id.equals(other.getId())) {
			return false;
		}
		if (numberOfReads != other.getNumberOfReads()) {
			return false;
		}
		return true;
	}
    
    
}
