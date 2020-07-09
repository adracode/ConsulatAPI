package fr.leconsulat.api.utils.minecraft.nbt;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.nbt.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class NBTMinecraft {
    
    private static Class<?> compoundTagClass;
    
    private static Method setCompoundTagInt;
    private static Method setCompoundTagString;
    
    static {
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
    
    private static final Constructor<?> newByteTag;
    private static final Constructor<?> newByteArrayTag;
    private static final Constructor<?> newCompoundTag;
    private static final Constructor<?> newDoubleTag;
    private static final Constructor<?> newFloatTag;
    private static final Constructor<?> newIntTag;
    private static final Constructor<?> newIntArrayTag;
    private static final Constructor<?> newListTag;
    private static final Constructor<?> newLongTag;
    private static final Constructor<?> newLongArrayTag;
    private static final Constructor<?> newShortTag;
    private static final Constructor<?> newStringTag;
    
    private static final Method putInCompound;
    private static final Method putInList;
    
    static{
        try {
            newByteTag = MinecraftReflection.getMinecraftClass("NBTTagByte").getConstructor(byte.class);
            newByteArrayTag = MinecraftReflection.getMinecraftClass("NBTTagByteArray").getConstructor(byte[].class);
            newCompoundTag = MinecraftReflection.getMinecraftClass("NBTTagCompound").getConstructor();
            newDoubleTag = MinecraftReflection.getMinecraftClass("NBTTagDouble").getConstructor(double.class);
            newFloatTag = MinecraftReflection.getMinecraftClass("NBTTagFloat").getConstructor(float.class);
            newIntTag = MinecraftReflection.getMinecraftClass("NBTTagInt").getConstructor(int.class);
            newIntArrayTag = MinecraftReflection.getMinecraftClass("NBTTagIntArray").getConstructor(int[].class);
            newListTag = MinecraftReflection.getMinecraftClass("NBTTagList").getConstructor();
            newLongTag = MinecraftReflection.getMinecraftClass("NBTTagLong").getConstructor(long.class);
            newLongArrayTag = MinecraftReflection.getMinecraftClass("NBTTagLongArray").getConstructor(long[].class);
            newShortTag = MinecraftReflection.getMinecraftClass("NBTTagShort").getConstructor(short.class);
            newStringTag = MinecraftReflection.getMinecraftClass("NBTTagString").getConstructor(String.class);
    
            putInCompound = MinecraftReflection.getMinecraftClass("NBTTagCompound").getDeclaredMethod("set",
                            String.class, MinecraftReflection.getMinecraftClass("NBTBase"));
            putInList = MinecraftReflection.getMinecraftClass("NBTTagList").getDeclaredMethod("add",
                            int.class, MinecraftReflection.getMinecraftClass("NBTBase"));
        } catch(NoSuchMethodException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    private static final Class<?> stringTag = MinecraftReflection.getNBTCompoundClass();
    
    public static Object byteToNMS(ByteTag tag){
        try {
            return newByteTag.newInstance(tag.getByte());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object byteArrayToNMS(ByteArrayTag tag){
        try {
            return newByteArrayTag.newInstance((Object)tag.getValue());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object compoundToNMS(CompoundTag tag){
        try {
            Object compound = newCompoundTag.newInstance();
            for(Map.Entry<String, Tag> entry : tag.getValue().entrySet()){
                putInCompound.invoke(compound, entry.getKey(), tagToNMS(entry.getValue()));
            }
            return compound;
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object doubleToNMS(DoubleTag tag){
        try {
            return newDoubleTag.newInstance(tag.getDouble());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object floatToNMS(FloatTag tag){
        try {
            return newFloatTag.newInstance(tag.getFloat());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object intToNMS(IntTag tag){
        try {
            return newIntTag.newInstance(tag.getInt());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object intArrayToNMS(IntArrayTag tag){
        try {
            return newIntArrayTag.newInstance((Object)tag.getValue());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object listToNMS(ListTag<? extends Tag> tag){
        try {
            Object list = newListTag.newInstance();
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
    
    public static Object longToNMS(LongTag tag){
        try {
            return newLongTag.newInstance(tag.getLong());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object longArrayToNMS(Tag tag){
        throw new UnsupportedOperationException();
    }
    
    public static Object shortToNMS(ShortTag tag){
        try {
            return newShortTag.newInstance(tag.getShort());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object stringToNMS(StringTag tag){
        try {
            return newStringTag.newInstance(tag.getValue());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
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
    
}
