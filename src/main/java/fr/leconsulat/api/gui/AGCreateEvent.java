package fr.leconsulat.api.gui;

/**
 * Event appelé lorsqu'un nouveau Gui est crée
 */
public class AGCreateEvent {

    private Object key;
    private AGui gui;
    private boolean cancelled = false;

    public AGCreateEvent(AGui gui, Object key){
        this.key = key;
        this.gui = gui;
    }

    public Object getKey(){
        return key;
    }

    public AGui getGui(){
        return gui;
    }

    public void setKeys(Object key){
        this.key = key;
    }

    public void setGui(AGui gui){
        this.gui = gui;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
}
