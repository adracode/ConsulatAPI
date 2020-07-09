package fr.leconsulat.api.nbt;


import fr.leconsulat.api.utils.MathUtils;

public final class FloatTag implements NumberTag {
	
	private final float value;
	
	public FloatTag(final float value) {
		this.value = value;
	}
	
	@Override
	public Float getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.FLOAT;
	}
	
	public long getLong() {
		return (long)value;
	}
	
	public int getInt() {
		return MathUtils.floor(value);
	}
	
	public short getShort() {
		return (short)(MathUtils.floor(value) & '\uffff');
	}
	
	public byte getByte() {
		return (byte)(MathUtils.floor(value) & 255);
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
		return "FloatTag{" +
				"value=" + value +
				'}';
	}
}
