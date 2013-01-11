package org.jcvi.common.core.seq.trace.fastq;

import org.jcvi.common.core.util.Builder;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code FastqRecordBuilder} is a {@link Builder} implementation
 * that can create new instances of {@link FastqRecord}s.
 * @author dkatzel
 *
 */
public final class FastqRecordBuilder implements Builder<FastqRecord>{
	private String comments=null;
	private final String id;
	private final NucleotideSequence basecalls;
	private final QualitySequence qualities;
	/**
	 * Create a new instance of {@link FastqRecordBuilder}
	 * with the given required parameters.
	 * @param id the id of this fastq record;
	 * can not be null but may contain whitespace.
	 * @param basecalls the {@link NucleotideSequence} for this fastq record;
	 * can not be null.
	 * @param qualities the {@link QualitySequence} for this fastq record;
	 * can not be null.
	 * @throws NullPointerException if any parameters are null.
	 * @throws IllegalArgumentException if the length of the
	 *  {@link NucleotideSequence} and {@link QualitySequence}
	 *  don't match.
	 */
	public FastqRecordBuilder(String id, NucleotideSequence basecalls, QualitySequence qualities){
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		if(basecalls ==null){
			throw new NullPointerException("basecalls can not be null");
		}
		if(qualities ==null){
			throw new NullPointerException("qualities can not be null");
		}
		long basecallLength = basecalls.getLength();
		long qualityLength = qualities.getLength();
		if(basecallLength !=qualityLength){
			throw new IllegalArgumentException(
					String.format("basecalls and qualities must have the same length! %d vs %d", 
							basecallLength, qualityLength));
		}
		this.id = id;
		this.basecalls = basecalls;
		this.qualities=qualities;
	}
	/**
	 * Add the given String to this fastq record as a comment
	 * which will get returned by {@link FastqRecord#getComment()}.
	 * Calling this method more than once will cause the last value to
	 * overwrite the previous value.
	 * @param comments the comment to make for this record;
	 * if this is set to null (the default) then this
	 * record has no comment.
	 * @return this.
	 */
	public FastqRecordBuilder comment(String comments){
		this.comments = comments;
		return this;
	}
	/**
	 * Create a new {@link FastqRecord} instance using the given
	 * parameters.
	 * @return a new {@link FastqRecord}; will never be null.
	 */
	@Override
	public FastqRecord build() {
		if(comments ==null){
			new UncommentedFastqRecord(id, basecalls, qualities);
		}
		return new CommentedFastqRecord(id, basecalls, qualities,comments);
	}
}
