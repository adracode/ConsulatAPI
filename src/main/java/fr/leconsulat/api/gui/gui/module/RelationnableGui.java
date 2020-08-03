package fr.leconsulat.api.gui.gui.module;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.event.GuiRemoveEvent;
import fr.leconsulat.api.gui.gui.BaseGui;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Relationnable;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class RelationnableGui implements Relationnable {
    
    private Relationnable gui;
    
    private @Nullable Relationnable father;
    private final @NotNull Map<Object, Relationnable> children = new HashMap<>();
    
    public RelationnableGui(Relationnable gui){
        this.gui = gui;
    }
    
    @Override
    public boolean hasFather(){
        return father != null;
    }
    
    @NotNull
    @Override
    public Relationnable getFather(){
        if(father == null){
            throw new NullPointerException("Father is null, use hasFather before calling method");
        }
        return father;
    }
    
    @Override
    public Relationnable setFather(@Nullable Relationnable father){
        this.father = father;
        if(father != null){
            setTitle();
            if(isBackButton()){
                setItem((getLine() - 1) * 9, IGui.getItem("§cRetour", -1, Material.RED_STAINED_GLASS_PANE));
            }
        }
        return this;
    }
    
    @Override
    public void addChild(Object key, @NotNull Relationnable gui){
        gui.setFather(this.gui);
        children.put(key, gui);
    }
    
    @Override
    public @NotNull Relationnable getChild(Object key){
        Relationnable child = getLegacyChild(key);
        if(child == null){
            child = createChild(key);
            if(child == null){
                throw new NullPointerException("Child couldn't be created");
            }
            addChild(key, child);
            child.onCreate();
        }
        return child;
    }
    
    @Override
    public Collection<Relationnable> getChildren(){
        return Collections.unmodifiableCollection(children.values());
    }
    
    @Override
    public @Nullable Relationnable createChild(@Nullable Object key){
        return gui.createChild(key);
    }
    
    @Override
    public @Nullable Relationnable getLegacyChild(@Nullable Object key){
        return children.get(key);
    }
    
    @Override
    public void removeChild(Object key){
        children.remove(key);
    }
    
    @Override
    public IGui getBaseGui(){
        return gui;
    }
    
    @Override
    public @NotNull BaseGui setDeco(@NotNull Material type, int... slots){
        return gui.setDeco(type, slots);
    }
    
    @Override
    public void setDisplayName(int slot, @NotNull String name){
        gui.setDisplayName(slot, name);
    }
    
    @Override
    public void setDescription(int slot, @NotNull String... description){
        gui.setDescription(slot, description);
    }
    
    @Override
    public void setType(int slot, @NotNull Material material){
        gui.setType(slot, material);
    }
    
    @Override
    public void setGlowing(int slot, boolean glow){
        gui.setGlowing(slot, glow);
    }
    
    @NotNull
    @Override
    public IGui setItem(@NotNull GuiItem item){
        return gui.setItem(item);
    }
    
    @NotNull
    @Override
    public IGui setItem(int slot, @Nullable GuiItem item){
        return gui.setItem(slot, item);
    }
    
    @Override
    public void moveItem(int from, int to){
        gui.moveItem(from, to);
    }
    
    @Override
    public void moveItem(int from, @NotNull IGui guiTo, int to){
        gui.moveItem(from, guiTo, to);
    }
    
    @Override
    @Nullable
    public GuiItem getItem(int slot){
        return gui.getItem(slot);
    }
    
    @Override
    public void open(@NotNull ConsulatPlayer player){
        gui.open(player);
    }
    
    @Override
    @NotNull
    public String getName(){
        return gui.getName();
    }
    
    @Override
    public void setName(String name){
        gui.setName(name);
        for(IGui child : children.values()){
            child.setTitle();
        }
    }
    
    @Override
    public String buildInventoryTitle(){
        if(hasFather()){
            return getFather().getName() + " > " + gui.getName();
        }
        return gui.getName();
    }
    
    @Override
    public void setTitle(){
        gui.setTitle();
    }
    
    @Override
    public void removeItem(int slot){
        gui.removeItem(slot);
    }
    
    @Override
    public @NotNull Inventory getInventory(){
        return gui.getInventory();
    }
    
    @Override
    public @NotNull List<GuiItem> getItems(){
        return gui.getItems();
    }
    
    @Override
    public void onCreate(){
        gui.onCreate();
    }
    
    @Override
    public void onOpen(GuiOpenEvent event){
        gui.onOpen(event);
    }
    
    @Override
    public void onClose(GuiCloseEvent event){
        gui.onClose(event);
    }
    
    @Override
    public void onClick(GuiClickEvent event){
        gui.onClick(event);
    }
    
    @Override
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
