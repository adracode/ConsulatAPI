package fr.leconsulat.api.nbt;

import java.util.Arrays;

public final class IntArrayTag implements Tag {
	
	private final int[] value;
	
	public IntArrayTag(final int[] value) {
		this.value = value;
	}
	
	@Override
	public int[] getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) {
			return true;
		}
		if(!(o instanceof IntArrayTag)){
			return false;
		}
		IntArrayTag that = (IntArrayTag)o;
		return Arrays.equals(value, that.value);
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(value);
	}
}
