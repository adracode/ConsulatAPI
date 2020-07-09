package fr.leconsulat.api.nbt;

import java.util.Arrays;

public final class ByteArrayTag implements Tag {
	
	private final byte[] value;

	public ByteArrayTag(final byte[] value) {
		this.value = value;
	}
	
	@Override
	public byte[] getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.BYTE_ARRAY;
	}
	
	@Override
	public String toString(){
		return "ByteArrayTag{" +
				"value=" + Arrays.toString(value) +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof ByteArrayTag)) return false;
		ByteArrayTag that = (ByteArrayTag)o;
		return Arrays.equals(value, that.value);
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(value);
	}
}
