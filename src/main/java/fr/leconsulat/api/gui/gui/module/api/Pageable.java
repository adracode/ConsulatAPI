package fr.leconsulat.api.gui.gui.module.api;

import fr.leconsulat.api.gui.event.*;
import fr.leconsulat.api.gui.gui.IGui;

public interface Pageable extends IGui {
    
    int getPage();
    
    void setPage(int page);
    
    MainPage getMainPage();
    
    void setMainPage(MainPage mainPage);
    
    void onPageCreated(GuiCreateEvent event, Pageable pageGui);
    
    void onPageClick(GuiClickEvent event, Pageable pageGui);
    
    void onPageOpen(GuiOpenEvent event, Pageable pageGui);
    
    void onPageClose(GuiCloseEvent event, Pageable pageGui);
    
    void onPageRemoved(GuiRemoveEvent event, Pageable pageGui);
    
}
