package fr.leconsulat.api.nbt;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class CompoundTag implements Tag {
	
	private static final long serialVersionUID = 6306315439190476192L;
	
	private final Map<String, Tag> value;

	public CompoundTag(Map<String, Tag> value) {
		this.value = new HashMap<>(value);
	}
	
	public CompoundTag() {
		this.value = new HashMap<>();
	}
	
	public boolean has(String name){
		return value.containsKey(name);
	}
	
	public void put(String name, Tag tag){
		value.put(name, tag);
	}
	
	public void putByteArray(String name, byte[] array){
		put(name, new ByteArrayTag(array));
	}
	
	public void putByte(String name, byte b){
		put(name, new ByteTag(b));
	}
	
	public void putDouble(String name, double d){
		put(name, new DoubleTag(d));
	}
	
	public void putFloat(String name, float f){
		put(name, new FloatTag(f));
	}
	
	public void putIntArray(String name, int[] array){
		put(name, new IntArrayTag(array));
	}
	
	public void putInt(String name, int i){
		put(name, new IntTag(i));
	}
	
	public void putLong(String name, long l){
		put(name, new LongTag(l));
	}
	
	public void putShort(String name, short s){
		put(name, new ShortTag(s));
	}
	
	public void putString(String name, String s){
		put(name, new StringTag(s));
	}
	
	public void putUUID(String name, UUID uuid){
		putString(name, uuid.toString());
	}
	
	public CompoundTag getCompound(String name){
		return (CompoundTag)value.get(name);
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public <T extends Tag> List<T> getList(String name, Class<T> expected){
		ListTag<T> listTag = ((ListTag<T>)value.get(name));
		if(listTag.getElementType() != NBTType.END && listTag.getElementType().getTagClass() != expected){
			throw new IllegalArgumentException("Expected: " + expected + ", current: " + listTag.getElementType());
		}
		return listTag.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Tag> List<T> getList(String name, NBTType expected){
		ListTag<T> listTag = ((ListTag<T>)value.get(name));
		if(listTag.getElementType() != NBTType.END && listTag.getElementType() != expected){
			throw new IllegalArgumentException("Expected: " + expected + ", current: " + listTag.getElementType());
		}
		return listTag.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Tag> ListTag<T> getListTag(String name, NBTType expected){
		ListTag<T> listTag = ((ListTag<T>)value.get(name));
		if(listTag.getElementType() != NBTType.END && listTag.getElementType() != expected){
			throw new IllegalArgumentException("Expected: " + expected + ", current: " + listTag.getElementType());
		}
		return listTag;
	}
	
	public byte[] getByteArray(String name){
		return ((ByteArrayTag)value.get(name)).getValue();
	}
	
	public byte getByte(String name){
		return ((ByteTag)value.get(name)).getByte();
	}
	
	public double getDouble(String name){
		return ((DoubleTag)value.get(name)).getDouble();
	}
	
	public float getFloat(String name){
		return ((FloatTag)value.get(name)).getFloat();
	}
	
	public int[] getIntArray(String name){
		return ((IntArrayTag)value.get(name)).getValue();
	}
	
	public int getInt(String name){
		return ((IntTag)value.get(name)).getInt();
	}
	
	public long getLong(String name){
		return ((LongTag)value.get(name)).getLong();
	}
	
	public short getShort(String name){
		return ((ShortTag)value.get(name)).getShort();
	}
	
	public String getString(String name){
		return ((StringTag)value.get(name)).getValue();
	}
	
	public UUID getUUID(String name){
		return UUID.fromString(getString(name));
	}
	
	@Override
	public Map<String, Tag> getValue() {
		return Collections.unmodifiableMap(value);
	}
	
	@Override
	public NBTType getType(){
		return NBTType.COMPOUND;
	}
	
	@NotNull
	public Set<String> keys(){
		return Collections.unmodifiableSet(value.keySet());
	}
	
	public Tag remove(String key){
		return value.remove(key);
	}
	
	@Override
	public String toString(){
		return "CompoundTag{" +
				"value=" + value +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof CompoundTag)){
			return false;
		}
		return value.equals(((CompoundTag)o).value);
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
}
