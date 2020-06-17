package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.gui.PagedGui;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lorsqu'un nouveau Gui est crée
 */
public class PagedGuiCreateEvent<T> implements Cancellable {

    private final T data;
    private final PagedGui<T> pagedGui;
    private final int page;
    private boolean cancelled = false;

    public PagedGuiCreateEvent(PagedGui<T> pagedGui, T data, int page){
        this.data = data;
        this.pagedGui = pagedGui;
        this.page = page;
    }
    
    @NotNull
    public T getData(){
        return data;
    }
    
    public int getPage(){
        return page;
    }
    
    public Gui<T> getGui(){
        return pagedGui.getGui();
    }
    
    public PagedGui<T> getPagedGui(){
        return pagedGui;
    }
    
    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
   
}
