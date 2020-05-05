package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;

/**
 * Event appelé lorsqu'un nouveau Gui est crée
 */
public class GuiCreateEvent {

    private Object key;
    private Gui gui;
    private boolean cancelled = false;

    public GuiCreateEvent(Gui gui, Object key){
        this.key = key;
        this.gui = gui;
    }

    public Object getKey(){
        return key;
    }

    public Gui getGui(){
        return gui;
    }

    public void setKeys(Object key){
        this.key = key;
    }

    public void setGui(Gui gui){
        this.gui = gui;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
}
