package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.gui.PagedGui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Cancellable;

/**
 * Event appelé lorsqu'un Gui est ouvert
 */
public class GuiOpenEvent<T> implements Cancellable {

    private final ConsulatPlayer player;
    private final PagedGui<T> pagedGui;
    private final T data;
    private boolean cancelled;

    public GuiOpenEvent(ConsulatPlayer player, PagedGui<T> pagedGui, T data){
        this.player = player;
        this.pagedGui = pagedGui;
        this.data = data;
        this.cancelled = false;
    }
    
    public PagedGui<T> getPagedGui(){
        return pagedGui;
    }
    
    public Gui<T> getGui(){
        return pagedGui.getGui();
    }
    
    public T getData(){
        return data;
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
