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
 * Created on Apr 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SequenceFastaRecordUtil {
   
    
    private static final Pattern ID_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");
    
    private SequenceFastaRecordUtil(){}
    
    public static String parseCommentFromIdLine(String line) {
        final Matcher idMatcher = ID_LINE_PATTERN.matcher(line);
        if (idMatcher.find()){
            return idMatcher.group(3);
        }
        return null;
    }

    public static String parseIdentifierFromIdLine(String line) {
        final Matcher idMatcher = ID_LINE_PATTERN.matcher(line);
        if (idMatcher.find()){
            return idMatcher.group(1);
        }
        return null;
    }
    
    public static String removeWhitespace(CharSequence sequence) {
        String sequenceWithoutWhitespace = sequence.toString().replaceAll("\\s+", "");
        return sequenceWithoutWhitespace;
    }
}
