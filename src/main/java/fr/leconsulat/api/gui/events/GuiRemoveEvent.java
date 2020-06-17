package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import org.jetbrains.annotations.NotNull;

public class GuiRemoveEvent<T> {
    
    private final T data;
    private final Gui<T> gui;
    
    public GuiRemoveEvent(Gui<T> gui, T data){
        this.data = data;
        this.gui = gui;
    }
    
    @NotNull
    public T getData(){
        return data;
    }
    
    public Gui<T> getGui(){
        return gui;
    }
    
}
