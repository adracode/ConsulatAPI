package fr.leconsulat.api.player;

public interface Permission {
    
    String getPermission();
    
    static String[] toStringArray(Permission... permissions){
        if(permissions.length == 0){
            return new String[0];
        }
        String[] perms = new String[permissions.length];
        for(int i = 0; i < permissions.length; i++){
            perms[i] = permissions[i].getPermission();
        }
        return perms;
    }
    
}
