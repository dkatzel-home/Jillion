/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.sam.attribute;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.core.Is.is;

import org.jcvi.jillion.sam.SamAttributed;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
public class TestDefaultChainedVaidator {

	SamHeader header = createMock(SamHeader.class);
	SamRecord record = createMock(SamRecord.class);
	SamAttribute attr = createMock(SamAttribute.class);
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void passesFirstValidatorShouldCallSecond() throws InvalidAttributeException{
		SamAttributeValidator second = createMock(SamAttributeValidator.class);
		
		SamAttributeValidator first = new SamAttributeValidator() {
			
			@Override
			public void validate(SamHeader header, SamAttributed record, SamAttribute attribute)
					throws InvalidAttributeException {
				//no-op
				
			}
		};
		
		SamAttributeValidator sut = first.thenValidating(second);
		
		
		second.validate(header, record, attr);
		
		replay(second);
		sut.validate(header, record, attr);
		verify(second);
	}
	
	@Test
	public void firstValidatorThrowsShouldNotCallSecond() throws InvalidAttributeException{
		SamAttributeValidator second = createMock(SamAttributeValidator.class);
		
		InvalidAttributeException expected = new InvalidAttributeException("expected");
		expectedException.expect(is(expected));
		
		SamAttributeValidator first = new SamAttributeValidator() {
			
			@Override
			public void validate(SamHeader header, SamAttributed record, SamAttribute attribute)
					throws InvalidAttributeException {
				throw expected;
				
			}
		};
		
		SamAttributeValidator sut = first.thenValidating(second);
		
		
		replay(second);
		
		sut.validate(header, record, attr);
		verify(second);
	}
	
	@Test
	public void secondValidatorThrows() throws InvalidAttributeException{
		SamAttributeValidator second = createMock(SamAttributeValidator.class);
		
		InvalidAttributeException expected = new InvalidAttributeException("expected");
		expectedException.expect(is(expected));
		
		SamAttributeValidator first = new SamAttributeValidator() {
			
			@Override
			public void validate(SamHeader header, SamAttributed record, SamAttribute attribute)
					throws InvalidAttributeException {
				//no-op
			}
		};
		
		SamAttributeValidator sut = first.thenValidating(second);
		
		
		second.validate(header, record, attr);
		
		expectLastCall().andThrow(expected);
		
		replay(second);
		sut.validate(header, record, attr);
		verify(second);
	}
	
	@Test
	public void secondValidatorThrowsUsingLambda() throws InvalidAttributeException{
		
		InvalidAttributeException expected = new InvalidAttributeException("expected");
		expectedException.expect(is(expected));
		
		SamAttributeValidator first = new SamAttributeValidator() {
			
			@Override
			public void validate(SamHeader header, SamAttributed record, SamAttribute attribute)
					throws InvalidAttributeException {
				//no-op
			}
		};
		
		SamAttributeValidator sut = first.thenValidating((header, record, attr)->{ throw expected;});

		sut.validate(header, record, attr);
		
	}
}
