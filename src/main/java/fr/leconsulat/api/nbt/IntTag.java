package fr.leconsulat.api.nbt;

public final class IntTag implements NumberTag {
	
	private static final long serialVersionUID = -8716298528684169124L;
	
	private final int value;
	
	public IntTag(final int value) {
		this.value = value;
	}
	
	@Override
	public Integer getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.INT;
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
	
	@Override
	public String toString(){
		return "IntTag{" +
				"value=" + value +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) {
			return true;
		}
		if(!(o instanceof IntTag)) {
			return false;
		}
		return value == ((IntTag)o).value;
	}
	
	@Override
	public int hashCode(){
		return Integer.hashCode(value);
	}
}
