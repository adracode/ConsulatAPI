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
	
}
