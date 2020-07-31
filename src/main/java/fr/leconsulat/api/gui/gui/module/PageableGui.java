package fr.leconsulat.api.gui.gui.module;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.*;
import fr.leconsulat.api.gui.gui.BaseGui;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.MainPage;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class PageableGui implements Pageable {
    
    private int page;
    private MainPage mainPage;
    
    private IGui gui;
    
    PageableGui(MainPage mainPage, String name, int line, GuiItem... items){
        this.mainPage = mainPage;
        this.gui = new BaseGui(this, name, line, items);
        this.gui.setName(buildInventoryTitle());
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
    public void setMainPage(MainPage mainPage){
        this.mainPage = mainPage;
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
    public void onPageClose(GuiCloseEvent event, Pageable pageGui){
        mainPage.onPageClose(event, pageGui);
    }
    
    @Override
    public void onPageRemoved(GuiRemoveEvent event, Pageable pageGui){
        mainPage.onPageRemoved(event, pageGui);
    }
    
    @Override
    public IGui getBaseGui(){
        return gui;
    }
    
    @Override
    public @NotNull BaseGui setDeco(@NotNull Material type, int... slots){
        return gui.setDeco(type, slots);
    }
    
    public void setDisplayName(int slot, @NotNull String name){
        gui.setDisplayName(slot, name);
    }
    
    public void setDescription(int slot, @NotNull String... description){
        gui.setDescription(slot, description);
    }
    
    public void setType(int slot, @NotNull Material material){
        gui.setType(slot, material);
    }
    
    public void setGlowing(int slot, boolean glow){
        gui.setGlowing(slot, glow);
    }
    
    @NotNull
    public IGui setItem(@NotNull GuiItem item){
        return gui.setItem(item);
    }
    
    @NotNull
    public IGui setItem(int slot, @Nullable GuiItem item){
        return gui.setItem(slot, item);
    }
    
    public void moveItem(int from, int to){
        gui.moveItem(from, to);
    }
    
    public void moveItem(int from, @NotNull IGui guiTo, int to){
        gui.moveItem(from, guiTo, to);
    }
    
    public @Nullable GuiItem getItem(int slot){
        return gui.getItem(slot);
    }
    
    public void open(@NotNull ConsulatPlayer player){
        gui.open(player);
    }
    
    public @NotNull String getName(){
        return gui.getName();
    }
    
    public void setName(String name){
        gui.setName(name);
    }
    
    @Override
    public String buildInventoryTitle(){
        return mainPage.buildInventoryTitle();
    }
    
    @Override
    public void setTitle(){
        gui.setTitle();
    }
    
    public void removeItem(int slot){
        gui.removeItem(slot);
    }
    
    public @NotNull Inventory getInventory(){
        return gui.getInventory();
    }
    
    public @NotNull List<GuiItem> getItems(){
        return gui.getItems();
    }
    
    @Override
    public final void onCreate(){
    
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
    
    public void onRemove(GuiRemoveEvent event){
        mainPage.onRemove(event);
    }
    
    public boolean isModifiable(){
        return gui.isModifiable();
    }
    
    public void setModifiable(boolean modifiable){
        gui.setModifiable(modifiable);
    }
    
    public boolean isDestroyOnClose(){
        return gui.isDestroyOnClose();
    }
    
    public void setDestroyOnClose(boolean destroyOnClose){
        gui.setDestroyOnClose(destroyOnClose);
    }
    
    public boolean isBackButton(){
        return gui.isBackButton();
    }
    
    public void setBackButton(boolean backButton){
        gui.setBackButton(backButton);
    }
    
    @Override
    public boolean containsFakeItems(){
        return gui.containsFakeItems();
    }
    
    @Override
    public IGui setFakeItem(int slot, ItemStack item, ConsulatPlayer player){
        return gui.setFakeItem(slot, item, player);
    }
}
