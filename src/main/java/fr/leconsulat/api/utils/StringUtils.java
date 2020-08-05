package fr.leconsulat.api.utils;

public final class StringUtils {
    
    public static String capitalize(String str){
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
}
