package fr.leconsulat.api.nbt;

public final class ByteTag implements NumberTag {
	
	private final byte value;
	
	public ByteTag(final byte value) {
		this.value = value;
	}
	
	@Override
	public Byte getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.BYTE;
	}
	
	public long getLong() {
		return value;
	}
	
	public int getInt() {
		return value;
	}
	
	public short getShort() {
		return value;
	}
	
	public byte getByte() {
		return value;
	}
	
	public double getDouble() {
		return value;
	}
	
	public float getFloat() {
		return value;
	}
	
	public Number getAsNumber() {
		return value;
	}
	
	@Override
	public String toString(){
		return "ByteTag{" +
				"value=" + value +
				'}';
	}
}
