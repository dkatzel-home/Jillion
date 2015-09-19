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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.util.Date;

import org.jcvi.jillion.assembly.tigr.tasm.TasmVisitor.TasmVisitorCallback;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TasmContigVisitor {

	void visitConsensus(NucleotideSequence consensus);
	/**
	 * Optional GUID of the corresponding
	 * id from Celera Assembler.
	 * If Celera Assembler was not 
	 * used, then this method will
	 * not get visited.
	 * @param id the 10-digit GUID generated by the Celera Assembler
	 * for this contig.
	 */
	void visitCeleraId(long id);
	/**
	 * Visit the comments that explains
	 * where this contig belongs.  
	 * It is possible for
	 * multiple contigs in the assembly to have the same
	 * comments.
	 * @param bacId The bac id (sample id) that this contig belongs.
	 * Since the TIGR project database was created 
	 * projects moved away from being BAC based so
	 * even though this id is named "bac id" 
	 * it usually doesn't refer to a Bacterial Artificial
	 * Chromosome, but just a sample id. If this value
	 * is null, then there is no bac id for this contig.
	 * @param comment a String describing the chromosome or segment
	 * this contig is part of.  ; will not be null or
	 * empty. 
	 * @param commonName A String that 
	 * explains what this contig is. Often
	 * this value includes the part of the comment
	 * from and additional ids (for example bac id).
	 * May be null which means no comment.
	 * @param assemblyMethod a String explaining the method 
	 * used to create this contig.
	 * This value is often
	 * the name and version of the assembler or software program.
	 * @param isCircular is this contig circular.
	 */
	void visitComments(Integer bacId, String comment, String commonName, String assemblyMethod, boolean isCircular);

	/**
	 * Visit coverage information about this contig.
	 * @param numberOfReads the number of reads in this contig.
	 * @param avgCoverage the average coverage.
	 */
	void visitCoverageData(int numberOfReads, float avgCoverage);
	/**
	 * Visit the info about the last time this contig 
	 * was edited in the TIGR Project Database.
	 * @param username the user name who made the last edit.
	 * @param editDate the {@link Date} when that edit was made.
	 */
	void visitLastEdited(String username, Date editDate);
	
	TasmContigReadVisitor visitRead(String readId, long gappedStartOffset, Direction dir, Range validRange);
	
	/**
	 * The parser has stopped 
	 * parsing but has not
	 * actually finished the parsing this contig,
	 * this will happen only if 
	 * a visitor calls {@link TasmVisitorCallback#halt()}.
	 */
	void halted();
	/**
	 * The entire contig has been visited.
	 */
	void visitEnd();
}
