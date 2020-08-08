package fr.leconsulat.api.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.*;

public class ObjectUtils {
    
    public static Object deepCopy(Object object){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(object);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return objInputStream.readObject();
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static void shallowCopy(Class<?> classToCopy, Object from, Object to){
        for(Field field : ReflectionUtils.getAllDeclaredFields(classToCopy)){
            ReflectionUtils.setField(field, to, ReflectionUtils.getDeclaredField(from, field));
        }
    }
    
    public static String getDeepContent(Object o){
        StringBuilder builder = new StringBuilder("Classes: ");
        for(Class<?> c : ReflectionUtils.getAllSuperClasses(o.getClass())){
            builder.append(c).append(", ");
        }
        builder.deleteCharAt(builder.length() - 1).append("\n");
        for(Field field : ReflectionUtils.getAllDeclaredFields(o.getClass())){
            builder.append(field.getName()).append(":").append(ReflectionUtils.getDeclaredField(o, field)).append("\n");
        }
        return builder.toString();
    }
    
    public static String getShallowContent(Object o){
        StringBuilder builder = new StringBuilder("Classes: ");
        for(Class c : ReflectionUtils.getAllSuperClasses(o.getClass())){
            builder.append(c).append(", ");
        }
        builder.deleteCharAt(builder.length() - 1).append("\n");
        for(Field field : o.getClass().getDeclaredFields()){
            Object fieldContent = ReflectionUtils.getDeclaredField(o, field);
            builder.append(field.getName()).append(":").append(fieldContent instanceof Object[] ? Arrays.toString((Object[])fieldContent) : fieldContent).append("\n");
        }
        return builder.toString();
    }
    
    public static <T> T randomInSet(Set<T> set){
        int random = new Random().nextInt(set.size());
        int i = 0;
        for(T t : set){
            if(i++ == random){
                return t;
            }
        }
        return null;
    }
    
    public static <T> List<T> removeElementArray(T[] array, T toRemove){
        List<T> newArray = new ArrayList<>();
        for(T t : array){
            if(t != toRemove){
                newArray.add(t);
            }
        }
        return newArray;
    }
    
    public static <T,S> List<List<?>> removeElementArrayInterfer(T[] arrayToRemoveElement, T toRemove, S[] arrayToInterfer){
        List<T> newArray = new ArrayList<>();
        List<S> newArrayInterfer = new ArrayList<>();
        List<Integer> positionsRemove = new ArrayList<>();
        List<List<?>> result = new ArrayList<>();
        for(int i = 0, arrayToRemoveElementLength = arrayToRemoveElement.length; i < arrayToRemoveElementLength; i++){
            if(arrayToRemoveElement[i] != toRemove){
                newArray.add(arrayToRemoveElement[i]);
                positionsRemove.add(i);
            }
        }
        result.add(newArray);
        for(int i = 0, arrayToInterferLength = arrayToInterfer.length; i < arrayToInterferLength; i++){
            S array = arrayToInterfer[i];
            if(!positionsRemove.contains(i)){
                newArrayInterfer.add(array);
            }
        }
        result.add(newArrayInterfer);
        return result;
    }
    
    public static void crash(){
        throw new NullPointerException();
    }
    
    public static int[] toIntArray(List<Integer> list){
        int[] array = new int[list.size()];
        int index = -1;
        for(Integer i : list){
            array[++index] = i;
        }
        return array;
    }
    
    public static String toString(Object object){
        return object.getClass().getName() + "@" + Integer.toHexString(object.hashCode());
    }
}
