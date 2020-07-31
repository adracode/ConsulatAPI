package fr.leconsulat.api.gui.gui.module;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.event.GuiRemoveEvent;
import fr.leconsulat.api.gui.gui.BaseGui;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Datable;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class DatableGui<T> implements Datable<T> {
    
    private final @Nullable T data;
    private final Datable<T> gui;
    
    public DatableGui(@NotNull Datable<T> gui, @Nullable T data){
        this.gui = gui;
        this.data = data;
    }
    
    @Override
    public @Nullable T getData(){
        return data;
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
    
    public @NotNull IGui setItem(@NotNull GuiItem item){
        return gui.setItem(item);
    }
    
    public @NotNull IGui setItem(int slot, @Nullable GuiItem item){
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
    
    public String buildInventoryTitle(){
        return gui.buildInventoryTitle();
    }
    
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
    public void onCreate(){
        gui.onCreate();
    }
    
    public void onOpen(GuiOpenEvent event){
        gui.onOpen(event);
    }
    
    public void onClose(GuiCloseEvent event){
        gui.onClose(event);
    }
    
    public void onClick(GuiClickEvent event){
        gui.onClick(event);
    }
    
    public void onRemove(GuiRemoveEvent event){
        gui.onRemove(event);
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
