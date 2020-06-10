package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Cancellable;

/**
 * Event appelé lorsque qu'un Gui est fermé
 */
public class GuiCloseEvent implements Cancellable {

    private final ConsulatPlayer player;
    private final Gui gui;
    private final Object key;
    private Object fatherKey;
    private boolean openFatherGui;
    private boolean cancelled;
    
    public GuiCloseEvent(ConsulatPlayer player, Gui gui, Object key, Object fatherKey, boolean openFatherGui){
        this.player = player;
        this.gui = gui;
        this.key = key;
        this.fatherKey = fatherKey;
        this.openFatherGui = openFatherGui;
    }
    
    public Gui getGui(){
        return gui;
    }
    
    public ConsulatPlayer getPlayer(){
        return player;
    }
    
    public boolean isOpenFatherGui(){
        return openFatherGui;
    }
    
    public void setOpenFatherGui(boolean openFatherGui){
        this.openFatherGui = openFatherGui;
    }
    
    public Object getKey(){
        return key;
    }
    
    public Object getFatherKey(){
        return fatherKey;
    }
    
    public void setFatherKey(Object fatherKey){
        this.fatherKey = fatherKey;
    }
    
    @Override
    public boolean isCancelled(){
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
}
