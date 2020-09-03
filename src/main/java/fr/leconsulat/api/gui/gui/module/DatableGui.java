package fr.leconsulat.api.gui.gui.module;

import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Datable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DatableGui<Gui extends IGui & Datable<T>, T> implements Datable<T> {
    
    private final @Nullable T data;
    private final Gui gui;
    
    public DatableGui(@NotNull Gui gui, @Nullable T data){
        this.gui = gui;
        this.data = data;
    }
    
    @Override
    public @Nullable T getData(){
        return data;
    }
    
    @Override
    public IGui getGui(){
        return gui;
    }
    
}
