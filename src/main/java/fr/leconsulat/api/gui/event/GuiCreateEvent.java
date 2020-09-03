package fr.leconsulat.api.gui.event;

import fr.leconsulat.api.gui.gui.IGui;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lorsqu'un nouveau PagedGui est crée
 */
public class GuiCreateEvent {
    
    private final @NotNull IGui gui;
    
    public GuiCreateEvent(@NotNull IGui gui){
        this.gui = gui;
    }
    
    public @NotNull IGui getGui(){
        return gui;
    }
}
