package fr.leconsulat.api.utils.minecraft.nbt;

import com.comphenix.protocol.utility.MinecraftReflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    
    private static final Class<?> stringTag = MinecraftReflection.getNBTCompoundClass();
    
}
