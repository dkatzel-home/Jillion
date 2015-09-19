/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilters;

public class TestDefaultFastQFileDataStore extends AbstractTestFastQFileDataStore{
    @Override
    protected FastqDataStore createFastQFileDataStore(File file, FastqQualityCodec qualityCodec) throws IOException {
    	FastqParser parser = new FastqFileParserBuilder(file)
    								.hasComments(true)
    								.hasMultilineSequences(true)
    								.build();
        return DefaultFastqFileDataStore.create(parser,qualityCodec,DataStoreFilters.alwaysAccept(), null);
    }
    
}
