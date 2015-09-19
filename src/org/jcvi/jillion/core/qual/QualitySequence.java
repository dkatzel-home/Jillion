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
package org.jcvi.jillion.core.qual;

import org.jcvi.jillion.core.Sequence;

/**
 * {@code QualitySequence} is a marker interface
 * for {@link Sequence} implementations
 * that encode {@link PhredQuality} values.
 * @author dkatzel
 *
 *
 */
public interface QualitySequence extends Sequence<PhredQuality>{

	/**
     * Two {@link QualitySequence}s are equal
     * if they contain the same {@link PhredQuality}s 
     * in the same order.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object o);
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    int hashCode();
    
    
    /**
     * Create an new array of bytes of length {@link #getLength()}
     * where index in the array is the ith quality score stored
     * as a byte.  This method may be expensive to perform
     * depending on the size of the sequence and the encoding used.
     * @return a new byte array, never null.
     */
    byte[] toArray();
    /**
     * Get the average quality score as a double.
     * This calculation only works on a sequence
     * that is not empty.
     * @return the avg quality score as a double.
     * @throws ArithmeticException if the sequence length is 0.
     */
    double getAvgQuality() throws ArithmeticException;
    /**
     * Get the min {@link PhredQuality} in the 
     * Sequence.
     * @return a {@link PhredQuality} or {@code null}
     * if the sequence is empty.
     */
    PhredQuality getMinQuality();
    /**
     * Get the min {@link PhredQuality} in the 
     * Sequence.
     * @return a {@link PhredQuality} or {@code null}
     * if the sequence is empty.
     */
    PhredQuality getMaxQuality();
    /**
     *  /**
     * Create a new Builder object that is initialized
     * to the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     * 
     * @return a new Builder instance, will never be null.
     * @sincen 5.0
     */
    default QualitySequenceBuilder toBuilder(){
        return new QualitySequenceBuilder(this);
    }
}
