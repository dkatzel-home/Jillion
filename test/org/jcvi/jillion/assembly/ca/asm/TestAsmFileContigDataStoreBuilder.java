/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ca.asm;

import static org.easymock.EasyMock.createMock;

import java.io.File;

import org.jcvi.jillion.assembly.ca.frg.FragmentDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.junit.Test;
public class TestAsmFileContigDataStoreBuilder {

	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE(){
		new AsmFileContigDataStoreBuilder(null, createMock(FragmentDataStore.class));
	}
	@Test(expected = NullPointerException.class)
	public void nullFrgDataStoreShouldThrowNPE(){
		new AsmFileContigDataStoreBuilder(new File("."), (FragmentDataStore) null);
	}
	@Test(expected = NullPointerException.class)
	public void nullNucleotideDataStoreShouldThrowNPE(){
		new AsmFileContigDataStoreBuilder(new File("."), (NucleotideSequenceDataStore) null);
	}
}
