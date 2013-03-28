/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AbstractAceContigVisitor;
import org.jcvi.jillion.assembly.ace.AbstractAceFileVisitor;
import org.jcvi.jillion.assembly.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.ace.AceFileParser;
import org.jcvi.jillion.assembly.ace.AceFileVisitor;
import org.jcvi.jillion.assembly.ace.AceFileVisitorCallback;
import org.jcvi.jillion.core.Direction;

public class OnlyPrintForwardReadsOfASpecificContig {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		
		File aceFile = new File("path/to/ace/file");
		final String contigIdToPrint = "myContigId";

		AceFileVisitor visitor = new AbstractAceFileVisitor() {

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				if(contigId.equals(contigIdToPrint)){
					return new AbstractAceContigVisitor() {

						@Override
						public void visitAlignedReadInfo(String readId,
								Direction dir, int gappedStartOffset) {
							if(dir == Direction.REVERSE){
								System.out.printf("%s starts at offset %d%n",readId, gappedStartOffset);
							}
						}
						
					};
				}
				//otherwise skip
				return null;				
			}
			
		};
		
		AceFileParser.create(aceFile).accept(visitor);
	}

}
