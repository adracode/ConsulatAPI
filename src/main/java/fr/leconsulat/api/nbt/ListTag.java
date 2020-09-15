package fr.leconsulat.api.nbt;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ListTag<T extends Tag> implements Tag {
	
	private static final long serialVersionUID = -5298469483175161595L;
	
	private @NotNull NBTType type;
	private final List<T> value;
	
	public ListTag(){
		this(NBTType.END);
	}
	
	public ListTag(@NotNull NBTType type) {
		this(type, null);
	}
	
	public ListTag(@NotNull NBTType type, Collection<T> value) {
		this.type = type;
		this.value = value == null ? new ArrayList<>() : new ArrayList<>(value);
	}
	
	public NBTType getElementType(){
		return type;
	}
	
	public NBTType getType(){
		return NBTType.LIST;
	}
	
	public void addTag(T tag){
		if(type == NBTType.END){
			type = tag.getType();
		}
		if(tag.getClass() != type.getTagClass()){
			throw new IllegalArgumentException();
		}
		value.add(tag);
	}
	
	@Override
	public List<T> getValue() {
		return Collections.unmodifiableList(value);
	}
	
	@Override
	public String toString(){
		return "ListTag{" +
				"type=" + type +
				", value=" + value +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof ListTag)){
			return false;
		}
		ListTag<? extends Tag> listTag = (ListTag<? extends Tag>)o;
		return type.equals(listTag.type) &&
				value.equals(listTag.value);
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
}
