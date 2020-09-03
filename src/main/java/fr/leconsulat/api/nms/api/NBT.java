package fr.leconsulat.api.nms.api;

import fr.leconsulat.api.nbt.*;

public interface NBT {
    
    Tag nmsToTag(Object nbtBase);
    
    Object toNMS(Tag tag);
    
    StringTag nmsToString(Object tag);
    
    Object toNMS(StringTag tag);
    
    ShortTag nmsToShort(Object tag);
    
    Object toNMS(ShortTag tag);
    
    LongTag nmsToLong(Object tag);
    
    Object toNMS(LongTag tag);
    
    ListTag<? extends Tag> nmsToList(Object tag);
    
    Object toNMS(ListTag<? extends Tag> tag);
    
    IntArrayTag nmsToIntArray(Object tag);
    
    Object toNMS(IntArrayTag tag);
    
    IntTag nmsToInt(Object tag);
    
    Object toNMS(IntTag tag);
    
    FloatTag nmsToFloat(Object tag);
    
    Object toNMS(FloatTag tag);
    
    DoubleTag nmsToDouble(Object tag);
    
    Object toNMS(DoubleTag tag);
    
    CompoundTag nmsToCompound(Object tag);
    
    Object toNMS(CompoundTag tag);
    
    ByteArrayTag nmsToByteArray(Object tag);
    
    Object toNMS(ByteArrayTag tag);
    
    ByteTag nmsToByte(Object tag);
    
    Object toNMS(ByteTag tag);
}
