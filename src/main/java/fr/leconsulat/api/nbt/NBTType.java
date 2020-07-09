package fr.leconsulat.api.nbt;

import java.io.Serializable;

public enum  NBTType implements Serializable {
    
    END(EndTag.class),
    BYTE(ByteTag.class),
    SHORT(ShortTag.class),
    INT(IntTag.class),
    LONG(LongTag.class),
    FLOAT(FloatTag.class),
    DOUBLE(DoubleTag.class),
    BYTE_ARRAY(ByteArrayTag.class),
    STRING(StringTag.class),
    LIST(ListTag.class),
    COMPOUND(CompoundTag.class),
    INT_ARRAY(IntArrayTag.class);
    
    private static NBTType[] types = values();
    public static NBTType byId(int id){
        return types[id];
    }
    
    private final Class<? extends Tag> tagClass;
    
    NBTType(Class<? extends Tag> tagClass){
        this.tagClass = tagClass;
    }
    
    public int getId(){
        return ordinal();
    }
    
    public Class<? extends Tag> getTagClass(){
        return tagClass;
    }
}
