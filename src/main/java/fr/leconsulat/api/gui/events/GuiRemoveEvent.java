package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import org.jetbrains.annotations.NotNull;

public class GuiRemoveEvent<T> {
    
    private final Gui<T> gui;
    
    public GuiRemoveEvent(Gui<T> gui){
        this.gui = gui;
    }
    
    @NotNull
    public T getData(){
        return gui.getData();
    }
    
    public Gui<T> getGui(){
        return gui;
    }
    
}
