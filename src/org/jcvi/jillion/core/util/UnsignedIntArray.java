package org.jcvi.jillion.core.util;

import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;


public class UnsignedIntArray {

	private final int[] array;
	
	public UnsignedIntArray(int[] array){
		if(array ==null){
			throw new NullPointerException("array can not be null");
		}
		this.array = array;
	}
	
	/**
	 * The number of elements in the array.
	 * @return an int always >=0.
	 */
	public int getLength(){
		return array.length;
	}
	/**
	 * Get the unsigned byte value
	 * of the given 0-based index.
	 * @param i the 0-based index; must be >=0 and < length
	 * @return the unsigned byte value;
	 * will always be >=0.
	 */
	public long get(int i){
		return IOUtil.toUnsignedInt(array[i]);
	}
	/**
	 * Set the given value to the given
	 * 0-based index in the array.
	 * @param i
	 * @param value the unsigned value to set.
	 */
	public void put(int i, long value){
		array[i] =IOUtil.toSignedInt(value);
	}


	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UnsignedIntArray)) {
			return false;
		}
		UnsignedIntArray other = (UnsignedIntArray) obj;
		if (!Arrays.equals(array, other.array)) {
			return false;
		}
		return true;
	}
	
	
	
}
