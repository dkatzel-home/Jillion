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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jcvi.common.core.Rangeable;

public final class DefaultConsensusAceTagBuilder implements org.jcvi.common.core.util.Builder<ConsensusAceTag>{
        private final List<String> comments = new ArrayList<String>();
        private final String id;
        
        private final Rangeable location;
        private final boolean isTransient;
        
        private final String type;
        private final String creator;
        private final Date creationDate;
        private final StringBuilder dataBuilder = new StringBuilder();
        
        /**
         * @param id
         * @param type
         * @param creator
         * @param creationDate
         * @param location
         * @param isTransient
         * @param data
         */
        public DefaultConsensusAceTagBuilder(String id, String type, String creator,
                Date creationDate, Rangeable location, boolean isTransient) {
            this.id = id;
            this.location = location;
            this.isTransient = isTransient;
            this.type = type;
            this.creator = creator;
            this.creationDate = new Date(creationDate.getTime());
        }

        public DefaultConsensusAceTagBuilder appendData(String data){
            dataBuilder.append(data);
            return this;
        }

        public DefaultConsensusAceTagBuilder addComment(String comment){
            comments.add(comment);
            return this;
        }

        @Override
        public ConsensusAceTag build() {
            return new DefaultConsensusAceTagBuilderImpl(id, type, creator, 
                    creationDate, location, dataBuilder.toString(), 
                    comments, isTransient);
        }


private static final class DefaultConsensusAceTagBuilderImpl extends AbstractDefaultPlacedAceTag implements ConsensusAceTag{
    private final List<String> comments;
    
    private DefaultConsensusAceTagBuilderImpl(String id, String type, String creator,
            Date creationDate, Rangeable location, String data, List<String> comments, boolean isTransient) {
        super(id, type, creator, creationDate, location, data, isTransient);
        this.comments = comments;
    }
    @Override
    public List<String> getComments() {
        return comments;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((comments == null) ? 0 : comments.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConsensusAceTag)) {
            return false;
        }
        ConsensusAceTag other = (ConsensusAceTag) obj;
        
        
        if(!getId().equals(other.getId())) {
            return false;
        }
        if (isTransient() != other.isTransient()) {
            return false;
        }
        if (asRange() == null) {
            if (other.asRange() != null) {
                return false;
            }
        } else if (!asRange().equals(other.asRange())) {
            return false;
        }
        if (comments == null) {
            if (other.getComments() != null) {
                return false;
            }
        } else if (!comments.equals(other.getComments())) {
            return false;
        }
        return true;
    }




    @Override
    public String toString() {
        return "DefaultConsensusAceTag [comments=" + comments + ", range()="
                + asRange() + ", getId()=" + getId() + ", isTransient()=" + isTransient()
                + ", getCreationDate()=" + getCreationDate()
                + ", getCreator()=" + getCreator() + ", getData()=" + getData()
                + ", getType()=" + getType() + "]";
    }
}
}