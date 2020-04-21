package fr.leconsulat.api.player;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class CustomRank {
    
    private ChatColor colorPrefix = null;
    private String prefix = null;
    private ChatColor colorName = null;
    private String cache = null;
    
    public CustomRank(){
    }
    
    public CustomRank(String rank){
        if(rank == null){
            return;
        }
        int open = rank.indexOf('['), close = rank.indexOf(']');
        this.colorPrefix = ChatColor.getByChar(rank.charAt(1));
        this.prefix = rank.substring(open, close + 2);
        this.colorName = ChatColor.getByChar(rank.charAt(close + 3));
    }
    
    public String getCustomRank(){
        if(cache == null){
            if(colorPrefix == null || prefix == null || colorName == null){
                return null;
            } else {
                cache = colorPrefix + prefix + colorName;
            }
        }
        return cache;
    }
    
    public String getCustomPrefix(){
        return colorPrefix + prefix;
    }
    
    public void setColorPrefix(ChatColor colorPrefix){
        this.colorPrefix = colorPrefix;
        cache = null;
    }
    
    public void setPrefix(String prefix){
        this.prefix = "[" + prefix + "] ";
        cache = null;
    }
    
    public void setColorName(ChatColor colorName){
        this.colorName = colorName;
        cache = null;
    }
    
    public void reset(){
        this.prefix = null;
        this.cache = null;
        this.colorName = null;
        this.colorPrefix = null;
    }
    
    @Override
    public String toString(){
        return "CustomRank{" +
                "colorPrefix=" + colorPrefix +
                ", prefix='" + prefix + '\'' +
                ", colorName=" + colorName +
                ", cache='" + cache + '\'' +
                '}';
    }
}
