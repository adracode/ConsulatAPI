package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Cancellable;

/**
 * Event appelé lorsqu'un Gui est ouvert
 */
public class GuiOpenEvent<T> implements Cancellable {

    private final ConsulatPlayer player;
    private final Gui<T> gui;
    private final T Key;
    private boolean cancelled;

    public GuiOpenEvent(ConsulatPlayer player, Gui<T> gui, T key){
        this.player = player;
        this.gui = gui;
        Key = key;
        this.cancelled = false;
    }
    
    public Gui<T> getGui(){
        return gui;
    }
    
    public T getKey(){
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
