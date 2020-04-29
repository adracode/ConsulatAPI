package fr.leconsulat.api.gui;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

public class AGui implements InventoryHolder, Cloneable {

    private String name;
    private final byte lines;
    private Inventory gui;
    private Map<Byte, AGuiItem> items = new HashMap<>();
    private Object key;
    private Object fatherKey;
    private AGListener listener;
    
    public AGui(AGListener listener, String name, int lines, AGuiItem... items){
        if(listener == null){
            throw new NullPointerException("Listener cannot be null");
        }
        this.listener = listener;
        this.name = name;
        this.lines = (byte)lines;
        if(items != null){
            for(AGuiItem item : items){
                this.items.put(item.getSlot(), item);
            }
        }
    }
    
    @Override
    public AGui clone() throws CloneNotSupportedException{
        AGui tmp = (AGui) super.clone();
        tmp.items = new HashMap<>(this.items.size());
        for(AGuiItem item : items.values()){
            tmp.items.put(item.getSlot(), new AGuiItem(item));
        }
        return tmp;
    }
    
    /**
     * Changer le nom de l'item au slot visé
     * @param slot le slot de l'item
     * @param name le nom à mettre
     */
    public void setDisplayName(int slot, String name){
        this.getItem(slot).setDisplayName(name);
        this.update(slot);
    }
    
    /**
     * Changer la description de l'item au slot visé
     * @param slot le slot de l'item
     * @param description la description à mettre
     */
    public void setDescription(int slot, String... description){
        this.getItem(slot).setDescription(description);
        this.update(slot);
    }
    
    /**
     * Changer le type de l'item au slot visé
     * @param slot le slot de l'item
     * @param material le type à mettre
     */
    public void setType(int slot, Material material){
        this.getItem(slot).setType(material);
        this.update(slot);
    }
    
    /**
     * Changer l'effet de lueur de l'item au slot visé
     * @param slot le slot de l'item
     * @param glow true pour activé l'effet, false pour le désactivé
     */
    public void setGlowing(int slot, boolean glow){
        this.getItem(slot).setGlowing(glow);
        this.update(slot);
    }
    
    public Object getFatherKey(){
        return fatherKey;
    }
    
    public AGui setFatherKey(Object fatherKey){
        this.fatherKey = fatherKey;
        return this;
    }
    
    public Object getKey(){
        return key;
    }
    
    public void setKey(Object key){
        this.key = key;
    }

    public Collection<AGuiItem> getItems(){
        return items.values();
    }
    
    public Map<Byte, AGuiItem> getMap(){
        return Collections.unmodifiableMap(items);
    }
    
    /**
     * Ajouter un item au gui
     * @param item l'item à ajouter
     * @return le gui où l'item a été ajouté
     */
    public AGui addItem(AGuiItem item){
        this.items.put(item.getSlot(), item);
        return this;
    }
    
    /**
     * Déplacer un item
     *
     * Si le slot où l'item sera déplace n'est pas vide, l'item sera commuté avec lui
     * @param from le slot de l'item à déplacer
     * @param to le slot où l'item sera ddéplacé
     */
    public void moveItem(int from, int to){
        if(from == to){
            return;
        }
        AGuiItem itemFrom = getItem((byte)from);
        if(itemFrom == null){
            return;
        }
        itemFrom.setSlot((byte)to);
        AGuiItem itemTo = getItem(to);
        if(itemTo != null){
            items.put((byte)from, itemTo);
            gui.setItem(from, itemTo.getItem());
            itemTo.setSlot(from);
        } else {
            items.remove((byte)from);
            gui.setItem(from, null);
        }
        items.put((byte)to, itemFrom);
        gui.setItem(to, itemFrom.getItem());
    }
    
    /**
     * Renvoie l'item au slot visé
     * @param slot le slot visé
     * @return l'item au slot vidé
     */
    public AGuiItem getItem(int slot){
        return items.get((byte)slot);
    }
    
    /**
     * Créer l'inventaire du gui
     * @return
     */
    public Inventory create(){
        String preName = this.listener.getFather() == null ?
                name :
                this.listener.getFather().getGui(this.getFatherKey()).getName() + " §e> " + name;
        if(preName.length() > 32){
            preName = name;
        }
        this.gui = Bukkit.createInventory(this, lines * 9, preName);
        for(AGuiItem item : items.values()){
            gui.setItem(item.getSlot(), item.getItem());
        }
        return this.gui;
    }
    
    public AGListener getListener(){
        return listener;
    }
    
    /**
     * Ouvre ce gui au joueur visé
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
     * @param slot le slot visé
     */
    public void update(int slot){
        byte byteSlot = (byte)slot;
        if(gui != null){
            AGuiItem item = items.get(byteSlot);
            if(item != null){
                if(item.getSlot() != byteSlot){
                    items.remove(byteSlot);
                    items.put(byteSlot, item);
                }
                gui.setItem(slot, item.getItem());
            } else {
                gui.setItem(slot, null);
            }
        } else {
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
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * Supprime l'item au slot visé
     * @param slot le slot visé
     */
    public void removeItem(byte slot){
        this.items.remove(slot);
        this.update(slot);
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        AGui gui = (AGui)o;
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