package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.PagedGui;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lorsqu'un nouveau PagedGui est crée
 */
public class PagedGuiCreateEvent<T> {

    private T key;
    private final PagedGui<T> pagedGui;

    public PagedGuiCreateEvent(T key, PagedGui<T> pagedGui){
        this.key = key;
        this.pagedGui = pagedGui;
    }
    
    @NotNull
    public T getKey(){
        return key;
    }

    public void setKey(T key){
        this.key = key;
    }
    
    public PagedGui<T> getPagedGui(){
        return pagedGui;
    }
}
