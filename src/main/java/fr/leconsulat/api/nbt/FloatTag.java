package fr.leconsulat.api.nbt;

import fr.leconsulat.api.utils.MathUtils;

public final class FloatTag implements NumberTag {
	
	private static final long serialVersionUID = -872472607273277329L;
	
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
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof FloatTag)) {
			return false;
		}
		return Float.compare(((FloatTag)o).value, value) == 0;
	}
	
	@Override
	public int hashCode(){
		return Float.hashCode(value);
	}
}
