package fr.leconsulat.api.utils.minecraft.nbt;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.nbt.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class NBTMinecraft {
    
    private static Class<?> compoundTagClass;
    
    private static Method setCompoundTagInt;
    private static Method setCompoundTagString;
    
    static{
        compoundTagClass = MinecraftReflection.getNBTCompoundClass();
        try {
            setCompoundTagInt = compoundTagClass.getMethod("setInt", String.class, int.class);
            setCompoundTagString = compoundTagClass.getMethod("setString", String.class, String.class);
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }
    
    public static Object newCompoundTag(){
        try {
            return compoundTagClass.newInstance();
        } catch(InstantiationException | IllegalAccessException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static void setCompoundTagInt(Object compoundTag, String id, int value){
        try {
            setCompoundTagInt.invoke(compoundTag, id, value);
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
    
    public static void setCompoundTagString(Object compoundTag, String id, String value){
        try {
            setCompoundTagString.invoke(compoundTag, id, value);
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
    
    private static final Constructor<?>[] tagConstructors;
    private static final Method[] getTagValues;
    
    private static final Method putInCompound;
    private static final Method putInList;
    
    private static final Field getCompoundValue;
    private static final Field getListValue;
    
    private static final Method getType;
    
    static{
        try {
            tagConstructors = new Constructor[]{
                    MinecraftReflection.getMinecraftClass("NBTTagByte").getConstructor(byte.class),
                    MinecraftReflection.getMinecraftClass("NBTTagByteArray").getConstructor(byte[].class),
                    MinecraftReflection.getMinecraftClass("NBTTagCompound").getConstructor(),
                    MinecraftReflection.getMinecraftClass("NBTTagDouble").getConstructor(double.class),
                    MinecraftReflection.getMinecraftClass("NBTTagFloat").getConstructor(float.class),
                    MinecraftReflection.getMinecraftClass("NBTTagInt").getConstructor(int.class),
                    MinecraftReflection.getMinecraftClass("NBTTagIntArray").getConstructor(int[].class),
                    MinecraftReflection.getMinecraftClass("NBTTagList").getConstructor(),
                    MinecraftReflection.getMinecraftClass("NBTTagLong").getConstructor(long.class),
                    MinecraftReflection.getMinecraftClass("NBTTagLongArray").getConstructor(long[].class),
                    MinecraftReflection.getMinecraftClass("NBTTagShort").getConstructor(short.class),
                    MinecraftReflection.getMinecraftClass("NBTTagString").getConstructor(String.class)
            };
    
            getTagValues = new Method[]{
                    MinecraftReflection.getMinecraftClass("NBTTagByte").getMethod("asByte"),
                    MinecraftReflection.getMinecraftClass("NBTTagByteArray").getMethod("getBytes"),
                    MinecraftReflection.getMinecraftClass("NBTTagDouble").getMethod("asDouble"),
                    MinecraftReflection.getMinecraftClass("NBTTagFloat").getMethod("asFloat"),
                    MinecraftReflection.getMinecraftClass("NBTTagInt").getMethod("asInt"),
                    MinecraftReflection.getMinecraftClass("NBTTagIntArray").getMethod("getInts"),
                    MinecraftReflection.getMinecraftClass("NBTTagLong").getMethod("asLong"),
                    MinecraftReflection.getMinecraftClass("NBTTagLongArray").getMethod("getLongs"),
                    MinecraftReflection.getMinecraftClass("NBTTagShort").getMethod("asShort"),
                    MinecraftReflection.getMinecraftClass("NBTTagString").getMethod("asString")
            };
            
            putInCompound = MinecraftReflection.getMinecraftClass("NBTTagCompound").getMethod("set",
                    String.class, MinecraftReflection.getMinecraftClass("NBTBase"));
            putInList = MinecraftReflection.getMinecraftClass("NBTTagList").getMethod("add",
                    int.class, MinecraftReflection.getMinecraftClass("NBTBase"));
            
            getCompoundValue = MinecraftReflection.getMinecraftClass("NBTTagCompound").getDeclaredField("map");
            getCompoundValue.setAccessible(true);
            getListValue = MinecraftReflection.getMinecraftClass("NBTTagList").getDeclaredField("list");
            getListValue.setAccessible(true);
    
            getType = MinecraftReflection.getMinecraftClass("NBTBase").getMethod("getTypeId");
        } catch(NoSuchMethodException | NoSuchFieldException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object byteToNMS(ByteTag tag){
        try {
            return tagConstructors[0].newInstance(tag.getByte());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static ByteTag nmsToByte(Object tag){
        try {
            return new ByteTag((byte)getTagValues[0].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object byteArrayToNMS(ByteArrayTag tag){
        try {
            return tagConstructors[1].newInstance((Object)tag.getValue());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static ByteArrayTag nmsToByteArray(Object tag){
        try {
            return new ByteArrayTag((byte[])getTagValues[1].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object compoundToNMS(CompoundTag tag){
        try {
            Object compound = tagConstructors[2].newInstance();
            for(Map.Entry<String, Tag> entry : tag.getValue().entrySet()){
                putInCompound.invoke(compound, entry.getKey(), tagToNMS(entry.getValue()));
            }
            return compound;
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static CompoundTag nmsToCompound(Object tag){
        try {
            CompoundTag compoundTag = new CompoundTag();
            for(Map.Entry<String, ?> nbtBase : ((Map<String, ?>)getCompoundValue.get(tag)).entrySet()){
                compoundTag.put(nbtBase.getKey(), nmsToTag(nbtBase.getValue()));
            }
            return compoundTag;
        } catch(IllegalAccessException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object doubleToNMS(DoubleTag tag){
        try {
            return tagConstructors[3].newInstance(tag.getDouble());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static DoubleTag nmsToDouble(Object tag){
        try {
            return new DoubleTag((double)getTagValues[2].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object floatToNMS(FloatTag tag){
        try {
            return tagConstructors[4].newInstance(tag.getFloat());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static FloatTag nmsToFloat(Object tag){
        try {
            return new FloatTag((float)getTagValues[3].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object intToNMS(IntTag tag){
        try {
            return tagConstructors[5].newInstance(tag.getInt());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static IntTag nmsToInt(Object tag){
        try {
            return new IntTag((int)getTagValues[4].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object intArrayToNMS(IntArrayTag tag){
        try {
            return tagConstructors[6].newInstance((Object)tag.getValue());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static IntArrayTag nmsToIntArray(Object tag){
        try {
            return new IntArrayTag((int[])getTagValues[5].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object listToNMS(ListTag<? extends Tag> tag){
        try {
            Object list = tagConstructors[7].newInstance();
            List<? extends Tag> value = tag.getValue();
            for(int i = 0; i < value.size(); i++){
                putInList.invoke(list, i, tagToNMS(value.get(i)));
            }
            return list;
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static ListTag<? extends Tag> nmsToList(Object tag){
        try {
            ListTag<Tag> listTag = new ListTag<>();
            for(Object nbtBase : (List<?>)getListValue.get(tag)){
                Tag addTag = nmsToTag(nbtBase);
                listTag.addTag(addTag);
            }
            return listTag;
        } catch(IllegalAccessException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object longToNMS(LongTag tag){
        try {
            return tagConstructors[8].newInstance(tag.getLong());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static LongTag nmsToLong(Object tag){
        try {
            return new LongTag((long)getTagValues[6].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object shortToNMS(ShortTag tag){
        try {
            return tagConstructors[10].newInstance(tag.getShort());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static ShortTag nmsToShort(Object tag){
        try {
            return new ShortTag((short)getTagValues[8].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object stringToNMS(StringTag tag){
        try {
            return tagConstructors[11].newInstance(tag.getValue());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static StringTag nmsToString(Object tag){
        try {
            return new StringTag((String)getTagValues[9].invoke(tag));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object tagToNMS(Tag tag){
        switch(tag.getType()){
            case BYTE_ARRAY:
                return byteArrayToNMS((ByteArrayTag)tag);
            case BYTE:
                return byteToNMS((ByteTag)tag);
            case COMPOUND:
                return compoundToNMS((CompoundTag)tag);
            case DOUBLE:
                return doubleToNMS((DoubleTag)tag);
            case FLOAT:
                return floatToNMS((FloatTag)tag);
            case INT:
                return intToNMS((IntTag)tag);
            case INT_ARRAY:
                return intArrayToNMS((IntArrayTag)tag);
            case LIST:
                return listToNMS((ListTag<?>)tag);
            case LONG:
                return longToNMS((LongTag)tag);
            case SHORT:
                return shortToNMS((ShortTag)tag);
            case STRING:
                return stringToNMS((StringTag)tag);
            default:
                throw new UnsupportedOperationException();
            
        }
    }
    
    public static Tag nmsToTag(Object nbtBase){
        try {
            switch((int)(byte)getType.invoke(nbtBase)){
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
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
}
