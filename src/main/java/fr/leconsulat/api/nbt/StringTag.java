package fr.leconsulat.api.nbt;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class StringTag implements Tag {
	
	private final @NotNull String value;
	
	public StringTag(@NotNull final String value) {
		this.value = Objects.requireNonNull(value);
	}
	
	@Override
	public @NotNull String getValue() {
		return value;
	}
	
	@Override
	public NBTType getType(){
		return NBTType.STRING;
	}
	
	@Override
	public String toString(){
		return "StringTag{" +
				"value='" + value + '\'' +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof StringTag)){
			return false;
		}
		return value.equals(((StringTag)o).value);
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
}
