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
package org.jcvi.jillion.assembly.consed.phd;

import java.util.Date;

import org.jcvi.jillion.core.Range;
/**
 * A {@link PhdReadTagVisitor}
 * that collects the information about a single read tag
 * and then calls the protected method
 * visitPhdReadTag()
 * when the entire tag has been visited (this is known because {@link #visitEnd()}
 * as been called).
 * Subclasses are required to implement the abstract method 
 * to handle the completely visited read tag.
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractPhdReadTagVisitor implements PhdReadTagVisitor{

	private String type;
	private String source;
	private Range ungappedRange;
	private Date date;
	private String comment;
	private final StringBuilder freeFormDataBuilder = new StringBuilder();
	
	@Override
	public final void visitType(String type) {
		this.type = type;
	}

	@Override
	public final void visitSource(String source) {
		this.source = source;
		
	}

	@Override
	public final void visitUngappedRange(Range ungappedRange) {
		this.ungappedRange = ungappedRange;
	}

	@Override
	public final void visitDate(Date date) {
		this.date = new Date(date.getTime());		
	}

	@Override
	public final void visitComment(String comment) {
		this.comment=comment;
		
	}

	@Override
	public final void visitFreeFormData(String data) {
		this.freeFormDataBuilder.append(data);
		
	}

	@Override
	public final void visitEnd() {
		final String freeFormData;
		if(freeFormDataBuilder.length() ==0){
			//no free form data
			freeFormData =null;
		}else{
			freeFormData= freeFormDataBuilder.toString().trim();
		}
		visitPhdReadTag(type, source,ungappedRange, date, comment, freeFormData);
		
	}
	/**
	 * Visit a {@link PhdReadTag}.
	 * @param type the type of the tag.
	 * @param source the source of the tag.
	 * @param ungappedRange the {@link Range} on the read
	 * that spans this tag.
	 * 
	 * @param date the date this tag was generated.
	 * @param comment any additional comments.
	 * @param freeFormData any additional data as a string.
	 */
	protected abstract void visitPhdReadTag(String type, String source,
			Range ungappedRange, Date date, String comment, String freeFormData);
	/**
	 * Ignored by default, please
	 * override to get halted notification.
	 * {@inheritDoc}
	 */
	@Override
	public void halted() {
		//no-op		
	}

}
