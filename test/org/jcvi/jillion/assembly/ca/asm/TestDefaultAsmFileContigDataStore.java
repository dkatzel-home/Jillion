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
package org.jcvi.jillion.assembly.ca.asm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ca.asm.AsmContigDataStore;
import org.jcvi.jillion.assembly.ca.asm.DefaultAsmContigDataStore;
import org.jcvi.jillion.assembly.ca.frg.FragmentDataStore;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;

public class TestDefaultAsmFileContigDataStore extends AbstractTestAsmContigDataStore{

	@Override
	protected AsmContigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws IOException {
		return DefaultAsmContigDataStore.create(asmFile, DataStore.adapt(NucleotideSequenceDataStore.class, frgDataStore, 
				from -> from.getNucleotideSequence()),
		DataStoreFilters.alwaysAccept());
	}

}
