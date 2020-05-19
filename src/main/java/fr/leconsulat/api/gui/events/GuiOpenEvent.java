package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Cancellable;

/**
 * Event appelé lorsqu'un Gui est ouvert
 */
public class GuiOpenEvent implements Cancellable {

    private final ConsulatPlayer player;
    private final Gui gui;
    private final Object Key;
    private boolean cancelled;

    public GuiOpenEvent(ConsulatPlayer player, Gui gui, Object key){
        this.player = player;
        this.gui = gui;
        Key = key;
        this.cancelled = false;
    }
    
    public Gui getGui(){
        return gui;
    }
    
    public Object getKey(){
        return Key;
    }
    
    public ConsulatPlayer getPlayer(){
        return player;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
}
