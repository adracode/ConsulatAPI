package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.gui.PagedGui;
import org.jetbrains.annotations.NotNull;

public class PagedGuiRemoveEvent<T> {
    
    private final T data;
    private final PagedGui<T> pagedGui;
    private final int page;
    
    public PagedGuiRemoveEvent(PagedGui<T> pagedGui, T data, int page){
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
    
}
