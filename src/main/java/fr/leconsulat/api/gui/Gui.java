package fr.leconsulat.api.gui;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.gui.events.GuiCreateEvent;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.utils.ObjectUtils;
import fr.leconsulat.api.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Field;
import java.util.*;

public class Gui implements InventoryHolder {
    
    private static Field inventoryField;
    private static Field titleField;
    
    static{
        try {
            inventoryField = MinecraftReflection.getCraftBukkitClass("inventory.CraftInventory").getDeclaredField("inventory");
            inventoryField.setAccessible(true);
            titleField = MinecraftReflection.getCraftBukkitClass("inventory.CraftInventoryCustom$MinecraftInventory").getDeclaredField("title");
            titleField.setAccessible(true);
        } catch(NoSuchFieldException e){
            e.printStackTrace();
        }
        
    }
    
    private String name;
    private final byte lines;
    private Inventory gui;
    private Object key;
    private Object fatherKey;
    private GuiListener listener;
    private GuiItem[] items;
    
    public Gui(Object key, GuiListener listener, String name, int lines, GuiItem... items){
        if(listener == null){
            throw new NullPointerException("Listener cannot be null");
        }
        this.listener = listener;
        this.name = name;
        this.lines = (byte)lines;
        this.key = key;
        this.items = new GuiItem[this.lines * 9];
        String preName = this.listener.getFather() == null ?
                name :
                this.listener.getFather().getGui(this.getFatherKey()).getName() + " §e> " + name;
        if(preName.length() > 32){
            preName = name;
        }
        this.gui = Bukkit.createInventory(this, lines * 9, preName);
        for(GuiItem item : items){
            setItem(item);
        }
        GuiCreateEvent event = new GuiCreateEvent(this, key);
        listener.onCreate(event);
    }
    
    private Gui(Gui gui){
        this.name = gui.name;
        this.lines = gui.lines;
        this.listener = gui.listener;
        this.key = gui.key;
        this.fatherKey = gui.fatherKey;
        this.items = new GuiItem[this.lines * 9];
        this.gui = Bukkit.createInventory(this, lines * 9, name);
        for(GuiItem item : gui.items){
            if(item != null){
                this.setItem(item.clone());
            }
        }
    }
    
    public Gui copy(Object key){
        Gui copy = new Gui(this);
        copy.setKey(key);
        GuiCreateEvent event = new GuiCreateEvent(copy, key);
        listener.onCreate(event);
        return copy;
    }
    
    /**
     * Changer le nom de l'item au slot visé
     *
     * @param slot le slot de l'item
     * @param name le nom à mettre
     */
    public void setDisplayName(int slot, String name){
        GuiItem item = getItem(slot);
        if(item != null){
            item.setDisplayName(name);
            this.update(slot);
        }
    }
    
    /**
     * Changer la description de l'item au slot visé
     *
     * @param slot        le slot de l'item
     * @param description la description à mettre
     */
    public void setDescription(int slot, String... description){
        GuiItem item = getItem(slot);
        if(item != null){
            item.setDescription(description);
            this.update(slot);
        }
        
    }
    
    /**
     * Changer le type de l'item au slot visé
     *
     * @param slot     le slot de l'item
     * @param material le type à mettre
     */
    public void setType(int slot, Material material){
        this.getItem(slot).setType(material);
        this.update(slot);
    }
    
    /**
     * Changer l'effet de lueur de l'item au slot visé
     *
     * @param slot le slot de l'item
     * @param glow true pour activé l'effet, false pour le désactivé
     */
    public void setGlowing(int slot, boolean glow){
        GuiItem item = getItem(slot);
        if(item != null){
            item.setGlowing(glow);
            this.update(slot);
        }
    }
    
    public Object getFatherKey(){
        return fatherKey;
    }
    
    public Gui setFatherKey(Object fatherKey){
        this.fatherKey = fatherKey;
        return this;
    }
    
    public Object getKey(){
        return key;
    }
    
    public void setKey(Object key){
        this.key = key;
    }
    
    public List<GuiItem> getItems(){
        return Collections.unmodifiableList(Arrays.asList(this.items));
    }
    
    public Gui setItem(int slot, GuiItem item){
        if(item == null){
            return setItem(null);
        }
        item.setSlot(slot);
        return setItem(item);
    }
    
    /**
     * Ajouter un item au gui
     *
     * @param item l'item à ajouter
     * @return le gui où l'item a été ajouté
     */
    public Gui setItem(GuiItem item){
        if(item == null){
            return this;
        }
        gui.setItem(item.getSlot(), item);
        items[item.getSlot()] = item;
        return this;
    }
    
    /**
     * Déplacer un item
     * <p>
     * Si le slot où l'item sera déplace n'est pas vide, l'item sera commuté avec lui
     *
     * @param from le slot de l'item à déplacer
     * @param to   le slot où l'item sera ddéplacé
     */
    public void moveItem(int from, int to){
        moveItem(from, this, to);
    }
    
    public void moveItem(int from, Gui guiTo, int to){
        if(from == to && this.equals(guiTo)){
            return;
        }
        GuiItem itemFrom = this.getItem(from);
        if(itemFrom == null){
            return;
        }
        itemFrom.setSlot(to);
        GuiItem itemTo = guiTo.getItem(to);
        if(itemTo != null){
            itemTo.setSlot(from);
            setItem(itemTo);
        } else {
            removeItem(from);
        }
        guiTo.setItem(itemFrom);
    }
    
    /**
     * Renvoie l'item au slot visé
     *
     * @param slot le slot visé
     * @return l'item au slot vidé
     */
    public GuiItem getItem(int slot){
        return items[slot];
    }
    
    public GuiListener getListener(){
        return listener;
    }
    
    /**
     * Ouvre ce gui au joueur visé
     *
     * @param player le joueur visé
     */
    public void open(ConsulatPlayer player){
        if(this.gui == null){
            return;
        }
        player.getPlayer().openInventory(this.gui);
        player.setCurrentlyOpen(this);
    }
    
    /**
     * Mets à jour le slot visé
     *
     * @param slot le slot visé
     */
    public void update(int slot){
        if(gui != null){
            GuiItem item = getItem(slot);
            if(item != null){
                gui.setItem(slot, item);
            } else {
                gui.setItem(slot, null);
            }
        }
    }
    
    public String getName(){
        return name;
    }
    
    public byte getLines(){
        return lines;
    }
    
    public Inventory getGui(){
        return gui;
    }
    
    /**
     * Changer le nom du gui
     *
     * @param name
     */
    public void setName(String name){
        this.name = name;
        try {
            titleField.set(inventoryField.get(gui), name);
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Supprime l'item au slot visé
     *
     * @param slot le slot visé
     */
    public void removeItem(int slot){
        gui.setItem(slot, null);
        items[slot] = null;
        this.update(slot);
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Gui gui = (Gui)o;
        return Objects.equals(key, gui.key) &&
                listener.equals(gui.listener);
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(key, listener);
    }
    
    @Override
    public Inventory getInventory(){
        return gui;
    }
}