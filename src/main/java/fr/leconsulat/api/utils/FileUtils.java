package fr.leconsulat.api.utils;

import java.io.File;

public class FileUtils {
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File loadFile(String name){
        if(name.contains("/")){
            StringBuilder dirBuilder = new StringBuilder();
            String[] split = name.split("/");
            for(int i = 0, splitLength = split.length; i < splitLength - 1; i++){
                dirBuilder.append(split[i]).append("/");
                File dir = new File(dirBuilder.toString());
                if(!dir.exists()){
                    dir.mkdir();
                }
            }
        }
    
        return new File(name);
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File loadFile(File parent, String name){
        if(!parent.exists()){
            parent.mkdir();
        }
        if(name.contains("/")){
            StringBuilder dirBuilder = new StringBuilder();
            String[] split = name.split("/");
            for(int i = 0, splitLength = split.length - 1; i < splitLength; i++){
                dirBuilder.append(split[i]).append("/");
                File dir = new File(parent, dirBuilder.toString());
                if(!dir.exists()){
                    dir.mkdir();
                }
            }
        }
        return new File(parent, name);
    }
    
    public static void deleteFile(File parent, String fileName){
        File file = new File(parent, fileName);
        if(file.exists()){
            file.delete();
        }
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File[] getFiles(File dir){
        if(!dir.exists()){
            dir.mkdir();
            return new File[0];
        }
        File[] files = dir.listFiles();
        if(files == null){
            return new File[0];
        }
        return files;
    }
    
}
