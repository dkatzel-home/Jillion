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
package org.jcvi.jillion.core.pos;


public final class Position{
	//most sanger traces only go to 15k or so
	private static final int INITIAL_CACHE_SIZE = 20000;
	private static final Position[] CACHE;

	private final int value;
	
	static{
		CACHE = new Position[INITIAL_CACHE_SIZE];
		for(int i=0; i< INITIAL_CACHE_SIZE; i++){
			CACHE[i] = new Position(i);
		}
	}
	
	public static Position valueOf(int value){
		if(value <0){
			throw new IllegalArgumentException("position value can not be negative");
		}
		if(value < CACHE.length){
			return CACHE[value];
		}
		return  new Position(value);
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}

	private Position(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Position)) {
			return false;
		}
		Position other = (Position) obj;
		if (value != other.value) {
			return false;
		}
		return true;
	}

}
