package fr.leconsulat.api.gui;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Field;
import java.util.*;

public class Gui<T> implements InventoryHolder {
    
    private static Field inventoryField;
    private static Field titleField;
    public static final Object NO_FATHER = new Object() {
        //@formatter:off
        @Override public int hashCode(){ return 0; }
        @Override public boolean equals(Object obj){ return obj == this; }
        //@formatter:on
    };
    
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
    private int page = 0;
    private PagedGui<T> pagedGui;
    private Inventory gui;
    private T key;
    private GuiListener<T> listener;
    private GuiItem[] items;
    private Gui<?> father;
    private Map<Object, Gui<?>> children = new HashMap<>();
    
    public Gui(T key, GuiListener<T> listener, String name, GuiItem... items){
        if(listener == null){
            throw new NullPointerException("Listener cannot be null");
        }
        this.listener = listener;
        this.name = name;
        this.key = key;
        this.items = new GuiItem[listener.getLine() * 9];
        this.gui = Bukkit.createInventory(this, listener.getLine() * 9, buildInventoryTitle());
        for(GuiItem item : items){
            setItem(item);
        }
    }
    
    void setPagedGui(PagedGui<T> pagedGui){
        this.pagedGui = pagedGui;
    }
    
    public void addChild(Object key, Gui<?> gui){
        children.put(key, gui);
    }
    
    public Gui<?> getChild(Object gui){
        return children.get(gui);
    }
    
    private Gui(Gui<T> gui){
        this.name = gui.name;
        this.page = gui.page;
        this.listener = gui.listener;
        this.key = gui.key;
        this.father = gui.father;
        this.pagedGui = gui.pagedGui;
        this.items = new GuiItem[listener.getLine() * 9];
        this.gui = Bukkit.createInventory(this, listener.getLine() * 9, buildInventoryTitle());
        for(GuiItem item : gui.items){
            if(item != null){
                this.setItem(item.clone());
            }
        }
    }
    
    public Gui<T> copy(){
        return new Gui<>(this);
    }
    
    public Gui<T> setDeco(Material type, int... slots){
        for(int slot : slots){
            setItem(new GuiItem(" ", (byte)slot, type));
        }
        return this;
    }
    
    @Override
    public String toString(){
        return "Gui{" +
                "name='" + name + '\'' +
                ", page=" + page +
                ", gui=" + gui +
                ", key=" + key +
                ", items=" + Arrays.toString(items) +
                '}';
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
    
    public Gui<?> getFather(){
        return father;
    }
    
    public Gui<?> setFather(Gui<?> father){
        this.father = father;
        setTitle();
        return this;
    }
    
    public T getKey(){
        return key;
    }
    
    public void setKey(T key){
        this.key = key;
    }
    
    public List<GuiItem> getItems(){
        return Collections.unmodifiableList(Arrays.asList(this.items));
    }
    
    public Gui<T> setItem(int slot, GuiItem item){
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
    public Gui<T> setItem(GuiItem item){
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
    
    public void moveItem(int from, Gui<T> guiTo, int to){
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
        setTitle();
    }
    
    private String buildInventoryTitle(){
        if(father == null){
            return name;
        } else {
            return father.getName() + " > " + name;
        }
    }
    
    private void setTitle(){
        String title = buildInventoryTitle();
        try {
            titleField.set(inventoryField.get(gui), title);
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
    
    public int getPage(){
        return page;
    }
    
    public void setPage(int page){
        this.page = page;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Gui<?> gui = (Gui<?>)o;
        return Objects.equals(key, gui.key) &&
                listener.equals(gui.listener) &&
                page == gui.page;
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(key, listener, page);
    }
    
    @Override
    public Inventory getInventory(){
        return gui;
    }
    
    public PagedGui<T> getPagedGui(){
        return pagedGui;
    }
}