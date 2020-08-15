package fr.leconsulat.api.gui.gui.module;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.*;
import fr.leconsulat.api.gui.gui.BaseGui;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.MainPage;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.gui.gui.module.api.Relationnable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class PageableGui implements Pageable {
    
    private int page;
    private MainPage mainPage;
    private Page gui;
    
    PageableGui(MainPage mainPage, String name, int line, GuiItem... items){
        this.mainPage = mainPage;
        this.gui = new Page(name, line, items);
        if(mainPage instanceof Relationnable && ((Relationnable)mainPage).hasFather() && gui.isBackButton()){
            gui.setItem((gui.getLine() - 1) * 9, GuiItem.BACK.clone());
        }
    }
    
    public final void onOpen(GuiOpenEvent event){
        onPageOpen(event, this);
    }
    
    public final void onClose(GuiCloseEvent event){
        onPageClose(event, this);
    }
    
    public final void onClick(GuiClickEvent event){
        onPageClick(event, this);
    }
    
    @Override
    public int getPage(){
        return page;
    }
    
    @Override
    public void setPage(int page){
        this.page = page;
    }
    
    @Override
    public MainPage getMainPage(){
        return mainPage;
    }
    
    @Override
    public IGui getGui(){
        return gui;
    }
    
    @Override
    public void onPageCreated(GuiCreateEvent event, Pageable pageGui){
        mainPage.onPageCreated(event, pageGui);
    }
    
    @Override
    public void onPageClick(GuiClickEvent event, Pageable pageGui){
        mainPage.onPageClick(event, pageGui);
    }
    
    @Override
    public void onPageOpen(GuiOpenEvent event, Pageable pageGui){
        mainPage.onPageOpen(event, pageGui);
    }
    
    @Override
    public void onPageOpened(GuiOpenEvent event, Pageable pageGui){
        mainPage.onPageOpened(event, pageGui);
    }
    
    @Override
    public void onPageClose(GuiCloseEvent event, Pageable pageGui){
        mainPage.onPageClose(event, pageGui);
    }
    
    @Override
    public void onPageRemoved(GuiRemoveEvent event, Pageable pageGui){
        mainPage.onPageRemoved(event, pageGui);
    }
    
    @NotNull
    @Override
    public Iterator<GuiItem> iterator(){
        return new GuiIterator();
    }
    
    private class GuiIterator implements Iterator<GuiItem> {
        
        private byte slot = -1;
        private ByteIterator iterator;
        
        public GuiIterator(){
            this.iterator = mainPage.getDynamicItems();
        }
        
        @Override
        public boolean hasNext(){
            return iterator.hasNext();
        }
        
        @Override
        public GuiItem next(){
            return gui.getItem(slot = iterator.nextByte());
        }
        
        @Override
        public void remove(){
            gui.removeItem(slot);
        }
    }
    
    public class Page extends BaseGui implements Pageable {
        
        public Page(@NotNull String name, int line, GuiItem... items){
            super(name, line, items);
        }
        
        @Override
        public int getPage(){
            return PageableGui.this.getPage();
        }
        
        @Override
        public void setPage(int page){
            PageableGui.this.setPage(page);
        }
        
        @Override
        public MainPage getMainPage(){
            return PageableGui.this.getMainPage();
        }
        
        @Override
        public IGui getGui(){
            return PageableGui.this.getGui();
        }
        
        @NotNull
        @Override
        public Iterator<GuiItem> iterator(){
            return PageableGui.this.iterator();
        }
        
        @Override
        public void onOpen(GuiOpenEvent event){
            PageableGui.this.onPageOpen(event, this);
        }
        
        @Override
        public void onOpened(GuiOpenEvent event){
            PageableGui.this.onPageOpened(event, this);
        }
        
        @Override
        public void onClose(GuiCloseEvent event){
            PageableGui.this.onPageClose(event, this);
        }
        
        @Override
        public void onClick(GuiClickEvent event){
            PageableGui.this.onPageClick(event, this);
        }
        
        @Override
        public String buildInventoryTitle(String title){
            if(mainPage instanceof Relationnable){
                return ((Relationnable)mainPage).buildInventoryTitle(title);
            }
            return super.buildInventoryTitle(title);
        }
    }
}
