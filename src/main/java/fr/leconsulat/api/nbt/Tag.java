package fr.leconsulat.api.nbt;

import java.io.Serializable;

public interface Tag extends Serializable {
	
	Object getValue();
	
	NBTType getType();
	
}
