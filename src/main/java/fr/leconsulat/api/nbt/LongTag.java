package fr.leconsulat.api.nbt;

public final class LongTag implements NumberTag {
	
	private final long value;
	
	public LongTag(final long value) {
		this.value = value;
	}
	
	@Override
	public Long getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.LONG;
	}
	
	public long getLong() {
		return value;
	}
	
	public int getInt() {
		return (int)(value);
	}
	
	public short getShort() {
		return (short)((int)(value & 65535L));
	}
	
	public byte getByte() {
		return (byte)((int)(value & 255L));
	}
	
	public double getDouble() {
		return (double)value;
	}
	
	public float getFloat() {
		return (float)value;
	}
	
	public Number getAsNumber() {
		return value;
	}
	
	@Override
	public String toString(){
		return "LongTag{" +
				"value=" + value +
				'}';
	}
}
