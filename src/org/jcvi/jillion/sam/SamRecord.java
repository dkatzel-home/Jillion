package org.jcvi.jillion.sam;

import java.util.EnumSet;
import java.util.Map;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.cigar.Cigar;

public class SamRecord {
	private final String queryName, referenceName, nextName;
	private final EnumSet<SamRecordFlags> flags;
	private int startOffset, nextOffset;
	private final byte mappingQuality;
	private final Cigar cigar;
	private final ReferenceMappedNucleotideSequence sequence;
	private final QualitySequence qualities;
	
	private final int observedTemplateLength;
	private final Map<SamAttributeKey, SamAttribute> attributes;
	
	private SamRecord(String queryName, EnumSet<SamRecordFlags> flags,
			String referenceName, int startOffset, byte mappingQuality, Cigar cigar,
			String nextName, int nextOffset, int observedTemplateLength,
			ReferenceMappedNucleotideSequence sequence, QualitySequence qualities,
			Map<SamAttributeKey, SamAttribute> attributes) {
		this.queryName = queryName;
		this.flags = flags;
		this.referenceName = referenceName;
		this.startOffset = startOffset;
		this.mappingQuality = mappingQuality;
		this.cigar = cigar;
		this.nextName = nextName;
		this.nextOffset = nextOffset;
		this.observedTemplateLength = observedTemplateLength;
		this.sequence = sequence;
		this.qualities = qualities;
		this.attributes = attributes;
	}
	
	public boolean isPrimary(){
		return 
				!(
					flags.contains(SamRecordFlags.SECONDARY_ALIGNMENT)
				|| flags.contains(SamRecordFlags.SUPPLEMENTARY_ALIGNMENT )
					);
	}
	
	public boolean useForAnalysis(){
		return !flags.contains(SamRecordFlags.SECONDARY_ALIGNMENT);
	}

	public String getQueryName() {
		return queryName;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public String getNextName() {
		return nextName;
	}

	public EnumSet<SamRecordFlags> getFlags() {
		return flags;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getNextOffset() {
		return nextOffset;
	}

	public byte getMappingQuality() {
		return mappingQuality;
	}

	public Cigar getCigar() {
		return cigar;
	}

	public ReferenceMappedNucleotideSequence getSequence() {
		return sequence;
	}

	public QualitySequence getQualities() {
		return qualities;
	}

	public int getObservedTemplateLength() {
		return observedTemplateLength;
	}
	
	public boolean hasAttribute(SamAttributeKey key){
		if(key==null){
			throw new NullPointerException("key can not be null");
		}
		return attributes.containsKey(key);
	}
	
	public SamAttribute getAttribute(SamAttributeKey key){
		return attributes.get(key);
	}
	
}
