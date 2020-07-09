package fr.leconsulat.api.nbt;

public final class IntTag implements NumberTag {
	
	private final int value;
	
	public IntTag(final int value) {
		this.value = value;
	}
	
	@Override
	public Integer getValue() {
		return value;
	}
	
	public long getLong() {
		return value;
	}
	
	public int getInt() {
		return value;
	}
	
	public short getShort() {
		return (short)(value & '\uffff');
	}
	
	public byte getByte() {
		return (byte)(value & 255);
	}
	
	public double getDouble() {
		return value;
	}
	
	public float getFloat() {
		return (float)value;
	}
	
	public Number getAsNumber() {
		return value;
	}
	
}
