package fr.leconsulat.api.gui.gui.template;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.*;
import fr.leconsulat.api.gui.gui.BaseGui;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.MainPageGui;
import fr.leconsulat.api.gui.gui.module.api.MainPage;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class PagedGui extends BaseGui implements MainPage {
    
    private MainPageGui mainPageGui;
    
    public PagedGui(@NotNull String name, int line, GuiItem... items){
        super(name, line, items);
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
    public void setTemplateItems(int... slots){
        mainPageGui.setTemplateItems(slots);
    }
    
    @Override
    public ByteIterator getDynamicItems(){
        return mainPageGui.getDynamicItems();
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
    public void setDisplayNamePages(int slot, @NotNull String name){
        mainPageGui.setDisplayName(slot, name);
    }
    
    @Override
    public void setDescriptionPages(int slot, @NotNull String... description){
        mainPageGui.setDescriptionPages(slot, description);
    }
    
    @Override
    public void setTypePages(int slot, @NotNull Material material){
        mainPageGui.setTypePages(slot, material);
    }
    
    @Override
    public void setGlowingPages(int slot, boolean glow){
        mainPageGui.setGlowingPages(slot, glow);
    }
    
    @Override
    public @NotNull IGui setItemAll(@NotNull GuiItem item){
        return mainPageGui.setItemAll(item);
    }
    
    @Override
    public @NotNull IGui setItemAll(int slot, @Nullable GuiItem item){
        return mainPageGui.setItemAll(slot, item);
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
    public void onPageCreated(GuiCreateEvent event, Pageable pageGui){
    }
    
    @Override
    public void onPageClick(GuiClickEvent event, Pageable pageGui){
    }
    
    @Override
    public void onPageOpen(GuiOpenEvent event, Pageable pageGui){
    }
    
    @Override
    public void onPageClose(GuiCloseEvent event, Pageable pageGui){
    }
    
    @Override
    public void onPageRemoved(GuiRemoveEvent event, Pageable pageGui){
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
