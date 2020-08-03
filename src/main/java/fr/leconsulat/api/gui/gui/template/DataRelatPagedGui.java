package fr.leconsulat.api.gui.gui.template;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.event.GuiCreateEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.gui.module.MainPageGui;
import fr.leconsulat.api.gui.gui.module.api.MainPage;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class DataRelatPagedGui<T> extends DataRelatGui<T> implements MainPage {
    
    private MainPageGui mainPageGui;
    
    public DataRelatPagedGui(T data, @NotNull String name, int line, GuiItem... items){
        super(data, name, line, items);
        this.mainPageGui = new MainPageGui(this);
        this.mainPageGui.onPageCreated(new GuiCreateEvent(this), mainPageGui);
    }
    
    @Override
    public Pageable getPage(int page){
        return mainPageGui.getPage(page);
    }
    
    @Override
    public void setDynamicItemsRange(int from, int to){
        mainPageGui.setDynamicItemsRange(from, to);
    }
    
    @Override
    public void setDynamicItems(int... slots){
        mainPageGui.setDynamicItems(slots);
    }
    
    @Override
    public void addPage(Pageable gui){
        mainPageGui.addPage(gui);
    }
    
    @Override
    public void removePage(int page){
        mainPageGui.removePage(page);
    }
    
    @Override
    public int numberOfPages(){
        return mainPageGui.numberOfPages();
    }
    
    @Override
    public int getCurrentPage(){
        return mainPageGui.getCurrentPage();
    }
    
    @Override
    public int getItemsNumber(){
        return mainPageGui.getItemsNumber();
    }
    
    @Override
    public byte getCurrentSlot(){
        return mainPageGui.getCurrentSlot();
    }
    
    @Override
    public void addItem(GuiItem item){
        mainPageGui.addItem(item);
    }
    
    @Override
    public void removeItem(int page, int slot){
        mainPageGui.removeItem(page, slot);
    }
    
    @Override
    public Pageable createPage(){
        return mainPageGui.createPage();
    }
    
    @Override
    public void removeAll(){
        mainPageGui.removeAll();
    }
    
    @Override
    public List<Pageable> getPages(){
        return mainPageGui.getPages();
    
    }
    
    @NotNull
    @Override
    public Iterator<GuiItem> iterator(){
        return mainPageGui.iterator();
    }
    
    @Override
    public int getPage(){
        return mainPageGui.getPage();
    }
    
    @Override
    public void setPage(int page){
        mainPageGui.setPage(page);
    }
    
    @Override
    public MainPage getMainPage(){
        return mainPageGui.getMainPage();
    }
    
    @Override
    public void setMainPage(MainPage mainPage){
        mainPageGui.setMainPage(mainPage);
    }
    
    @Override
    public final void onOpen(GuiOpenEvent event){
        onPageOpen(event, this);
    }
    
    @Override
    public void onOpened(GuiOpenEvent event){
        onPageOpened(event, this);
    }
    
    @Override
    public final void onClose(GuiCloseEvent event){
        onPageClose(event, this);
    }
    
    @Override
    public final void onClick(GuiClickEvent event){
        onPageClick(event, this);
    }
}
