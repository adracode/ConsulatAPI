package fr.leconsulat.api.utils;

public class ReflectionUtils {
    
    public static boolean isSuper(Class<?> searchedClass, Class<?> c){
        if(c == null){
            return false;
        }
        if(c.equals(searchedClass)){
            return true;
        }
        return isSuper(searchedClass, c.getSuperclass());
    }
    
}
