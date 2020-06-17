package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lorsqu'un nouveau PagedGui est crée
 */
public class GuiCreateEvent<T> {

    private final T data;
    private final Gui<T> gui;

    public GuiCreateEvent(T data, Gui<T> gui){
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
