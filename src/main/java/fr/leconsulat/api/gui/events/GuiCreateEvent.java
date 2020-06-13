package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.gui.PagedGui;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lorsqu'un nouveau Gui est crée
 */
public class GuiCreateEvent<T> implements Cancellable {

    private T key;
    private final PagedGui<T> pagedGui;
    private final Gui<T> gui;
    private final int page;
    private boolean cancelled = false;

    public GuiCreateEvent(Gui<T> gui, T key, PagedGui<T> pagedGui, int page){
        this.key = key;
        this.gui = gui;
        this.pagedGui = pagedGui;
        this.page = page;
    }
    
    @NotNull
    public T getKey(){
        return key;
    }

    public Gui<T> getGui(){
        return gui;
    }

    public void setKey(T key){
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
    
    public PagedGui<T> getPagedGui(){
        return pagedGui;
    }
}
