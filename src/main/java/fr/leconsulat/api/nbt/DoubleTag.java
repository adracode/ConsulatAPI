package fr.leconsulat.api.nbt;

import fr.leconsulat.api.utils.MathUtils;

public final class DoubleTag implements NumberTag {
	
	private static final long serialVersionUID = 8035817030577901095L;
	
	private final double value;
	
	public DoubleTag(final double value) {
		this.value = value;
	}
	
	@Override
	public Double getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.DOUBLE;
	}
	
	public long getLong() {
		return (long)Math.floor(value);
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
		return (float)value;
	}
	
	public Number getAsNumber() {
		return value;
	}
	
	@Override
	public String toString(){
		return "DoubleTag{" +
				"value=" + value +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) {
			return true;
		}
		if(!(o instanceof DoubleTag)) {
			return false;
		}
		return Double.compare(((DoubleTag)o).value, value) == 0;
	}
	
	@Override
	public int hashCode(){
		return Double.hashCode(value);
	}
}
