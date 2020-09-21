package fr.leconsulat.api.utils;

public final class StringUtils {
    
    public static String capitalize(String str){
        return Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }
    
    public static String join(String[] toJoin, String joiner){
        return join(toJoin, joiner, 0, toJoin.length);
    }
    
    public static String join(String[] toJoin, String joiner, int start){
        return join(toJoin, joiner, start, toJoin.length);
    }
    
    public static String join(String[] toJoin, String joiner, int start, int end){
        if(toJoin.length == 0){
            return "";
        }
        if(toJoin.length == 1){
            return toJoin[0];
        }
        if(start < 0 || end > toJoin.length){
            throw new IndexOutOfBoundsException();
        }
        StringBuilder builder = new StringBuilder(toJoin[start]);
        for(int i = start + 1; i < end; ++i){
            builder.append(joiner).append(toJoin[i]);
        }
        return builder.toString();
    }
    
    public static String join(String[] toJoin, char joiner){
        return join(toJoin, joiner, 0, toJoin.length);
    }
    
    public static String join(String[] toJoin, char joiner, int start){
        return join(toJoin, joiner, start, toJoin.length);
    }
    
    public static String join(String[] toJoin, char joiner, int start, int end){
        if(toJoin.length == 0){
            return "";
        }
        if(toJoin.length == 1){
            return toJoin[0];
        }
        if(start < 0 || end > toJoin.length){
            throw new IndexOutOfBoundsException();
        }
        StringBuilder builder = new StringBuilder(toJoin[start]);
        for(int i = start + 1; i < end; ++i){
            builder.append(joiner).append(toJoin[i]);
        }
        return builder.toString();
    }
    
}
