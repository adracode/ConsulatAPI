package fr.leconsulat.api.gui.gui.module.api;

import fr.leconsulat.api.gui.gui.IGui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Relationnable extends IGui {
    
    boolean hasFather();
    
    @NotNull Relationnable getFather();
    
    Relationnable setFather(@Nullable Relationnable father);
    
    void addChild(@Nullable Object key, @NotNull Relationnable gui);
    
    Relationnable getChild(@Nullable Object key);
    
    Relationnable createChild(@Nullable Object key);
    
    @Nullable Relationnable getLegacyChild(@Nullable Object key);
    
    void removeChild(@Nullable Object key);
    
}
