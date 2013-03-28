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
 * Created on Sep 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.archive;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/**
 * {@code TraceTypeCode} is all possible
 * values for {@link TraceInfoField#TRACE_TYPE_CODE}.
 * @author dkatzel
 *
 *
 */
public enum TraceTypeCode {
    
    
    /**
     * Sequences obtained using microarrays (also called DNA chips or gene
     * chips)
     */
    CHIP,
    /**
     * Sequences generated from the end of a large insert (BAC/PAC/Fosmid) or
     * cDNA clone
     */
    CLONEEND,
    /**
     * Single Pass Expressed Sequence Tag
     */
    EST,
    /**
     * High throughput SELEX
     */
    HTP_SELEX("HTP SELEX"),
    /**
     * Other than PCR, PrimerWalk, SHOTGUN or TRANSPOSON for FINISHING STRATEGY
     */
    OTHER,
    /**
     * Sequences obtained using templates generated by genomic Polymerase Chain
     * Reaction
     */
    PCR,
    /**
     * Sequences generated through a primer walking step
     */
    PrimerWalk,
    /**
     * Sequences obtained using templates generated by Reverse Transcriptase
     * Polymerase Chain Reaction
     */
    RT_PCR("RT-PCR"),
    /**
     * Shotgun sequencing of clones (genomic or cDNA)
     */
    SHOTGUN,
    /**
     * Sequences obtained using templates generated by transposons
     */
    TRANSPOSON,
    /**
     * Whole Chromosome Shotgun
     */
    WCS,
    /**
     * Whole Genome Shotgun
     */
    WGS,

    /**
     * Sequences obtained using 454 technology.
     * @deprecated Trace Archive RFC considers this code Obsolete
     */
    @Deprecated
    FOUR_FIVE_FOUR("454");
    
    
    private static final Map<String, TraceTypeCode> NAME_TO_TRACE_TYPE;
    static{
        NAME_TO_TRACE_TYPE = new HashMap<String, TraceTypeCode>();
        for(TraceTypeCode code : TraceTypeCode.values()){
            NAME_TO_TRACE_TYPE.put(code.toString().toUpperCase(), code);
        }
    }
    private final String actualName;
    private TraceTypeCode(){
        this(null);
    }
    private TraceTypeCode(String name){
        this.actualName = name;
    }
    
    public String toString(){
        if(actualName !=null){
            return actualName;
        }
        return this.name();
    }
    /**
     * Get the TraceTypeCode for the given string.
     * @param traceTypeCode (not null) string respesentation of a TraceTypeCode.
     * @return a TraceTypeCode
     * @throws NullPointerException if traceTypeCode is null.
     * @throws IllegalArgumentException if no TraceTypeCode could be found.
     */
    public static TraceTypeCode getTraceTypeCodeFor(String traceTypeCode){
        String code = traceTypeCode.toUpperCase(Locale.US);
        if(!NAME_TO_TRACE_TYPE.containsKey(code)){
            throw new IllegalArgumentException("unknown trace type code "+ traceTypeCode);
        }
        return NAME_TO_TRACE_TYPE.get(code);
    }
}
