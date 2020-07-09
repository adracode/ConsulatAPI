package fr.leconsulat.api.nbt;

public final class ShortTag implements NumberTag {
	
	private final short value;
	
	public ShortTag(final short value) {
		this.value = value;
	}
	
	@Override
	public Short getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.SHORT;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof ShortTag)){
			return false;
		}
		return value == ((ShortTag)o).value;
	}
	
	@Override
	public int hashCode(){
		return Short.hashCode(value);
	}
	
	@Override
	public long getLong(){
		return value;
	}
	
	@Override
	public int getInt(){
		return value;
	}
	
	@Override
	public short getShort(){
		return value;
	}
	
	@Override
	public byte getByte(){
		return (byte)(value & 255);
	}
	
	@Override
	public double getDouble(){
		return value;
	}
	
	@Override
	public float getFloat(){
		return value;
	}
	
	@Override
	public Number getAsNumber(){
		return value;
	}
	
	@Override
	public String toString(){
		return "ShortTag{" +
				"value=" + value +
				'}';
	}
}
