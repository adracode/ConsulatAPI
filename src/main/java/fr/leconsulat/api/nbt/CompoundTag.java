package fr.leconsulat.api.nbt;

//@formatter:off

/*
 * JNBT License
 *
 * Copyright (c) 2010 Graham Edgecombe
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     * Neither the name of the JNBT team nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

//@formatter:on

import java.util.*;

public final class CompoundTag implements Tag {
	
	private final Map<String, Tag> value;

	public CompoundTag(Map<String, Tag> value) {
		System.out.println("value.keySet() = " + value.keySet());
		this.value = new HashMap<>(value);
	}
	
	public CompoundTag() {
		this.value = new HashMap<>();
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
	public <T extends Tag> List<T> getList(String name, Class<T> expected){
		ListTag<T> listTag = ((ListTag<T>)value.get(name));
		if(listTag.getElementType().getTagClass() != expected){
			throw new IllegalArgumentException();
		}
		return listTag.getValue();
	}
	
	public boolean has(String name){
		return value.containsKey(name);
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
	
	@Override
	public String toString(){
		return "CompoundTag{" +
				"value=" + value +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof CompoundTag)){
			return false;
		}
		CompoundTag that = (CompoundTag)o;
		return value.equals(that.value);
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
}
