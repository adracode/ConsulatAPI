package fr.leconsulat.api.nbt;

import java.util.Arrays;

public final class IntArrayTag implements Tag {
	
	private static final long serialVersionUID = -149645358521606018L;
	
	private final int[] value;
	
	public IntArrayTag(final int[] value) {
		this.value = value;
	}
	
	@Override
	public int[] getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.INT_ARRAY;
	}
	
	@Override
	public String toString(){
		return "IntArrayTag{" +
				"value=" + Arrays.toString(value) +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) {
			return true;
		}
		if(!(o instanceof IntArrayTag)){
			return false;
		}
		return Arrays.equals(value, ((IntArrayTag)o).value);
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(value);
	}
}
