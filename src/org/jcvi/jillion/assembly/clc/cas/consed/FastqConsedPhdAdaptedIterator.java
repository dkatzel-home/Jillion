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

package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.assembly.ace.consed.ConsedUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.sanger.phd.ArtificialPhd;
import org.jcvi.jillion.trace.sanger.phd.Phd;
import org.jcvi.jillion.trace.sanger.phd.PhdUtil;


class FastqConsedPhdAdaptedIterator implements StreamingIterator<PhdReadRecord>{

	private final StreamingIterator<? extends FastqRecord> fastqIterator;
	private final Properties requiredComments;
	private final Date phdDate;
	private final File fastqFile;
	public FastqConsedPhdAdaptedIterator(StreamingIterator<? extends FastqRecord> fastqIterator,  File fastqFile,Date phdDate ){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.fastqIterator = fastqIterator;	
		this.phdDate = new Date(phdDate.getTime());
		this.fastqFile = fastqFile;
	}
	@Override
	public boolean hasNext() {
		return fastqIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		FastqRecord nextFastq = fastqIterator.next();
		String id = nextFastq.getId();
		Phd phd = ArtificialPhd.createNewbler454Phd(
				id, 
				nextFastq.getNucleotideSequence(), 
				nextFastq.getQualitySequence(),
				requiredComments);
		
		PhdInfo info = ConsedUtil.generateDefaultPhdInfoFor(fastqFile, id, phdDate);
		return new PhdReadRecord(phd, info);
	}

	@Override
	public void remove() {
		fastqIterator.remove();
		
	}
	@Override
	public void close() throws IOException {
		fastqIterator.close();
		
	}
}