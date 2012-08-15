package org.jcvi.common.core.seq.read.trace.sanger;

import org.jcvi.common.core.symbol.Symbol;

public final class Position implements Symbol{
	private static final int INITIAL_CACHE_SIZE = 1024;
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
	public String getName() {
		return Integer.toString(value);
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
