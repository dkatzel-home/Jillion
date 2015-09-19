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
package org.jcvi.jillion.core.util.iter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.iter.ChainedIterator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestChainedIterator {
    private Iterator<String> sut;
    
    List<String> stooges = Arrays.asList("larry","moe","curly");
    List<String> emptyList = Collections.emptyList();
    List<String> stooges2 = Arrays.asList("shemp","curly-joe","joe besser");
    @Before
    public void setup(){
        sut = ChainedIterator.create(Arrays.asList(
                stooges.iterator(),
                emptyList.iterator(),
                stooges2.iterator())
        );
    }
    
    @Test
    public void whenFirstIteratorFinishedShouldStartIteratingSecond(){
        List<String> expected = new ArrayList<String>();
        expected.addAll(stooges);
        expected.addAll(stooges2);
        assertTrue(sut.hasNext());
        for(int i=0; i< expected.size(); i++){
            assertEquals(expected.get(i), sut.next());
        }
        assertFalse(sut.hasNext());
        try{
            sut.next();
            fail("should throw no such element exception when iterators are empty");
        }catch(NoSuchElementException e){
            //expected
        }
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void removeShouldThrowException(){
        sut.remove();
    }
    
    @Test
    public void emptyIterators(){
        Iterator<String> iter = ChainedIterator.create(Collections.singleton(emptyList.iterator()));
        assertFalse(iter.hasNext());
    }
    
    @Test(expected = NullPointerException.class)
    public void nullParameterInConstructorShouldThrowNPE(){
        ChainedIterator.create(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullIteratorInListWillThrowNPE(){
        ChainedIterator.create(Arrays.asList(
                stooges.iterator(),
                null));
        
        
    }
}
