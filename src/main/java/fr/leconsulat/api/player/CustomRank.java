package fr.leconsulat.api.player;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CustomRank {
    
    private @Nullable ChatColor colorPrefix = null;
    private @Nullable String prefix = null;
    private @Nullable ChatColor colorName = null;
    private @Nullable String cache = null;
    
    public CustomRank(){
    }
    
    public CustomRank(@NotNull String rank){
        Objects.requireNonNull(rank, "rank");
        
        int open = rank.indexOf('['), close = rank.indexOf(']');
        this.colorPrefix = ChatColor.getByChar(rank.charAt(1));
        this.prefix = rank.substring(open, close + 2);
        this.colorName = ChatColor.getByChar(rank.charAt(close + 3));
    }
    
    public @Nullable String getCustomRank(){
        if(cache == null){
            if(colorPrefix == null || prefix == null || colorName == null){
                return null;
            } else {
                cache = colorPrefix + prefix + colorName;
            }
        }
        return cache;
    }
    
    public @Nullable String getCustomPrefix(){
        return colorPrefix + prefix;
    }
    
    public void setColorPrefix(@NotNull ChatColor colorPrefix){
        this.colorPrefix = Objects.requireNonNull(colorPrefix);
        cache = null;
    }
    
    public void setPrefix(@NotNull String prefix){
        this.prefix = "[" + Objects.requireNonNull(prefix) + "] ";
        cache = null;
    }
    
    public void setColorName(@NotNull ChatColor colorName){
        this.colorName = Objects.requireNonNull(colorName);
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
