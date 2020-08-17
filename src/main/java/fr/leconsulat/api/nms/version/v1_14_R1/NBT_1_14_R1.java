package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nbt.Tag;
import fr.leconsulat.api.nbt.*;
import fr.leconsulat.api.nms.api.NBT;
import net.minecraft.server.v1_14_R1.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class NBT_1_14_R1 implements NBT {
    
    private static final Field COMPOUND_MAP;
    
    static{
        try {
            COMPOUND_MAP = NBTTagCompound.class.getDeclaredField("map");
            COMPOUND_MAP.setAccessible(true);
        } catch(NoSuchFieldException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    @Override
    public Object toNMS(ByteTag tag){
        return new NBTTagByte(tag.getByte());
    }
    
    @Override
    public ByteTag nmsToByte(Object tag){
        return new ByteTag(((NBTTagByte)tag).asByte());
    }
    
    @Override
    public Object toNMS(ByteArrayTag tag){
        return new NBTTagByteArray(tag.getValue());
    }
    
    @Override
    public ByteArrayTag nmsToByteArray(Object tag){
        return new ByteArrayTag(((NBTTagByteArray)tag).getBytes());
    }
    
    @Override
    public Object toNMS(CompoundTag tag){
        NBTTagCompound compound = new NBTTagCompound();
        for(Map.Entry<String, Tag> entry : tag.getValue().entrySet()){
            compound.set(entry.getKey(), (NBTBase)toNMS(entry.getValue()));
        }
        return compound;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public CompoundTag nmsToCompound(Object tag){
        CompoundTag compoundTag = new CompoundTag();
        try {
            for(Map.Entry<String, NBTBase> nbtBase : ((Map<String, NBTBase>)COMPOUND_MAP.get(tag)).entrySet()){
                compoundTag.put(nbtBase.getKey(), nmsToTag(nbtBase.getValue()));
            }
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
        return compoundTag;
    }
    
    @Override
    public Object toNMS(DoubleTag tag){
        return new NBTTagDouble(tag.getValue());
    }
    
    @Override
    public DoubleTag nmsToDouble(Object tag){
        return new DoubleTag(((NBTTagDouble)tag).asDouble());
    }
    
    @Override
    public Object toNMS(FloatTag tag){
        return new NBTTagFloat(tag.getValue());
    }
    
    @Override
    public FloatTag nmsToFloat(Object tag){
        return new FloatTag(((NBTTagFloat)tag).asFloat());
    }
    
    @Override
    public Object toNMS(IntTag tag){
        return new NBTTagInt(tag.getValue());
    }
    
    @Override
    public IntTag nmsToInt(Object tag){
        return new IntTag(((NBTTagInt)tag).asInt());
    }
    
    @Override
    public Object toNMS(IntArrayTag tag){
        return new NBTTagIntArray(tag.getValue());
    }
    
    @Override
    public IntArrayTag nmsToIntArray(Object tag){
        return new IntArrayTag(((NBTTagIntArray)tag).getInts());
    }
    
    @Override
    public Object toNMS(ListTag<? extends Tag> tag){
        NBTTagList list = new NBTTagList();
        List<? extends Tag> value = tag.getValue();
        for(int i = 0; i < value.size(); i++){
            list.add((NBTBase)toNMS(value.get(i)));
        }
        return list;
    }
    
    @Override
    public ListTag<? extends Tag> nmsToList(Object tag){
        ListTag<Tag> listTag = new ListTag<>();
        for(NBTBase nbtBase : (NBTTagList)tag){
            listTag.addTag(nmsToTag(nbtBase));
        }
        return listTag;
    }
    
    @Override
    public Object toNMS(LongTag tag){
        return new NBTTagLong(tag.getValue());
    }
    
    @Override
    public LongTag nmsToLong(Object tag){
        return new LongTag(((NBTTagLong)tag).asLong());
    }
    
    @Override
    public Object toNMS(ShortTag tag){
        return new NBTTagShort(tag.getValue());
    }
    
    @Override
    public ShortTag nmsToShort(Object tag){
        return new ShortTag(((NBTTagShort)tag).asShort());
    }
    
    @Override
    public Object toNMS(StringTag tag){
        return new NBTTagString(tag.getValue());
    }
    
    @Override
    public StringTag nmsToString(Object tag){
        return new StringTag(((NBTTagString)tag).asString());
    }
    
    @Override
    public Object toNMS(Tag tag){
        switch(tag.getType()){
            case BYTE_ARRAY:
                return toNMS((ByteArrayTag)tag);
            case BYTE:
                return toNMS((ByteTag)tag);
            case COMPOUND:
                return toNMS((CompoundTag)tag);
            case DOUBLE:
                return toNMS((DoubleTag)tag);
            case FLOAT:
                return toNMS((FloatTag)tag);
            case INT:
                return toNMS((IntTag)tag);
            case INT_ARRAY:
                return toNMS((IntArrayTag)tag);
            case LIST:
                return toNMS((ListTag<?>)tag);
            case LONG:
                return toNMS((LongTag)tag);
            case SHORT:
                return toNMS((ShortTag)tag);
            case STRING:
                return toNMS((StringTag)tag);
            default:
                throw new UnsupportedOperationException();
            
        }
    }
    
    @Override
    public Tag nmsToTag(Object nbtBase){
        switch((int)((NBTBase)nbtBase).getTypeId()){
            case 1:
                return nmsToByte(nbtBase);
            case 7:
                return nmsToByteArray(nbtBase);
            case 10:
                return nmsToCompound(nbtBase);
            case 6:
                return nmsToDouble(nbtBase);
            case 5:
                return nmsToFloat(nbtBase);
            case 3:
                return nmsToInt(nbtBase);
            case 11:
                return nmsToIntArray(nbtBase);
            case 9:
                return nmsToList(nbtBase);
            case 4:
                return nmsToLong(nbtBase);
            case 2:
                return nmsToShort(nbtBase);
            case 8:
                return nmsToString(nbtBase);
            default:
                throw new UnsupportedOperationException();
        }
    }
    
}
