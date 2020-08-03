package fr.leconsulat.api.gui.gui.module.api;

import fr.leconsulat.api.gui.event.*;
import fr.leconsulat.api.gui.gui.IGui;

public interface Pageable extends IGui {
    
    int getPage();
    
    void setPage(int page);
    
    MainPage getMainPage();
    
    void setMainPage(MainPage mainPage);
    
    default void onPageCreated(GuiCreateEvent event, Pageable pageGui){
    }
    
    default void onPageClick(GuiClickEvent event, Pageable pageGui){
    }
    
    default void onPageOpen(GuiOpenEvent event, Pageable pageGui){
    }
    
    default void onPageOpened(GuiOpenEvent event, Pageable pageGui){
    }
    
    default void onPageClose(GuiCloseEvent event, Pageable pageGui){
    }
    
    default void onPageRemoved(GuiRemoveEvent event, Pageable pageGui){
    }
    
}
