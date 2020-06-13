package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.gui.PagedGui;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lorsqu'un nouveau PagedGui est crée
 */
public class PagedGuiCreateEvent implements Cancellable {

    private Object key;
    private final PagedGui pagedGui;
    private final Gui gui;
    private final int page;
    private boolean cancelled = false;

    public PagedGuiCreateEvent(Gui gui, Object key, PagedGui pagedGui, int page){
        this.key = key;
        this.gui = gui;
        this.pagedGui = pagedGui;
        this.page = page;
    }
    
    @NotNull
    public Object getKey(){
        return key;
    }

    public Gui getGui(){
        return gui;
    }

    public void setKey(Object key){
        this.key = key;
    }
    
    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
    
    public int getPage(){
        return page;
    }
    
    public PagedGui getPagedGui(){
        return pagedGui;
    }
}
