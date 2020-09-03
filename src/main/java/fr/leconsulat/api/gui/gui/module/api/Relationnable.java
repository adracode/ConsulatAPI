package fr.leconsulat.api.gui.gui.module.api;

import fr.leconsulat.api.gui.gui.IGui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Relationnable {
    
    boolean hasFather();
    
    @NotNull Relationnable getFather();
    
    Relationnable setFather(@Nullable Relationnable father);
    
    void addChild(@Nullable Object key, @NotNull Relationnable gui);
    
    Collection<Relationnable> getChildren();
    
    Relationnable getChild(@Nullable Object key);
    
    Relationnable createChild(@Nullable Object key);
    
    @Nullable Relationnable getLegacyChild(@Nullable Object key);
    
    void removeChild(@Nullable Object key);
    
    IGui getGui();
    
    void setTitle();
    
    String buildInventoryTitle(String title);
    
}
