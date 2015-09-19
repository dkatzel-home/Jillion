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
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

import java.io.File;

import org.jcvi.jillion.assembly.ca.frg.FragmentDataStore;
import org.jcvi.jillion.assembly.ca.frg.IndexedFragmentDataStore;

public class TestIndexedFragmentDataStore extends AbstractTestFragmentDataStore{

    @Override
    protected FragmentDataStore createFragmentDataStore(File file)
            throws Exception {
        return IndexedFragmentDataStore.create(file);
    }

}
