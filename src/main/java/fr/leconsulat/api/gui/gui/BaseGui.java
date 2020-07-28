package fr.leconsulat.api.gui.gui;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.event.GuiRemoveEvent;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BaseGui implements IGui {
    
    /* Champs utilisés pour modifier le titre de l'inventaire */
    private static Field inventoryField;
    private static Field titleField;
    
    /* Initialise les champs ci dessus */
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
    
    private static final GuiItem back = new GuiItem("§cRetour", (byte)-1, Material.RED_STAINED_GLASS_PANE);
    
    //Titre affiché dans l'inventaire
    @NotNull private String name;
    //Inventaire natif de Minecraft
    @NotNull private Inventory inventory;
    //Les différents items contenant dans le gui
    private GuiItem[] items;
    private boolean containsFakeItems = false;
    private int modifiers;
    
    public BaseGui(@NotNull String name, int line, GuiItem... items){
        this(null, name, line, items);
        setBackButton(true);
    }
    
    public boolean containsFakeItems(){
        return containsFakeItems;
    }
    
    /**
     * Créer un PagedGui
     *
     * @param name  Le nom affiché dans le Gui
     * @param items Les différents items composant le Gui
     */
    public BaseGui(IGui holder, @NotNull String name, int line, GuiItem... items){
        this.name = name;
        this.items = new GuiItem[line * 9];
        this.inventory = Bukkit.createInventory(holder == null ? this : holder, line * 9, buildInventoryTitle());
        for(GuiItem item : items){
            setItem(new GuiItem(item));
        }
    }
    
    /**
     * Constructeur par copie
     *
     * @param gui Le PagedGui à copier
     */
    private BaseGui(@NotNull BaseGui gui){
        this.name = gui.name;
        this.items = new GuiItem[gui.inventory.getSize()];
        this.inventory = Bukkit.createInventory(this, gui.inventory.getSize(), buildInventoryTitle());
        for(GuiItem item : gui.items){
            if(item != null){
                this.setItem(item.clone());
            }
        }
    }
    
    /**
     * Réalise une copie
     *
     * @return Le Gui copié
     */
    @NotNull
    public BaseGui copy(){
        return new BaseGui(this);
    }
    
    @Override
    public IGui getBaseGui(){
        return this;
    }
    
    @Override
    public int getLine(){
        return inventory.getSize() / 9;
    }
    
    /**
     * Place des items de "décoration" dans le Gui.
     * Un tel item n'aura pas de nom
     *
     * @param type  Le type de l'item
     * @param slots Les slots où placer les items
     * @return this
     */
    @NotNull
    @Override
    public BaseGui setDeco(@NotNull Material type, int... slots){
        for(int slot : slots){
            setItem(new GuiItem(" ", (byte)slot, type));
        }
        return this;
    }
    
    /**
     * Changer le nom de l'item au slot visé
     *
     * @param slot le slot de l'item
     * @param name le nom à mettre
     */
    @Override
    public void setDisplayName(int slot, @NotNull String name){
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
    @Override
    public void setDescription(int slot, @NotNull String... description){
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
    @Override
    public void setType(int slot, @NotNull Material material){
        GuiItem item = getItem(slot);
        if(item == null){
            throw new NullPointerException("Try to change type item at slot " + slot);
        }
        item.setType(material);
        this.update(slot);
    }
    
    /**
     * Changer l'effet de lueur de l'item au slot visé
     *
     * @param slot le slot de l'item
     * @param glow true pour activé l'effet, false pour le désactivé
     */
    @Override
    public void setGlowing(int slot, boolean glow){
        GuiItem item = getItem(slot);
        if(item != null){
            item.setGlowing(glow);
            this.update(slot);
        }
    }
    
    
    /**
     * Ajouter un item au gui
     *
     * @param item l'item à ajouter
     * @return le gui où l'item a été ajouté
     */
    @NotNull
    @Override
    public IGui setItem(@NotNull GuiItem item){
        return setItem(item.getSlot(), item);
    }
    
    @NotNull
    @Override
    public IGui setItem(int slot, @Nullable GuiItem item){
        if(item != null){
            item.setSlot(slot);
        }
        inventory.setItem(slot, item);
        items[slot] = item;
        return this;
    }
    
    @Override
    public IGui setFakeItem(int slot, ItemStack item, ConsulatPlayer player){
        GuiItem it = getItem(slot);
        if(it == null){
            return this;
        }
        it.addFakeItem(player.getUUID(), item);
        containsFakeItems = true;
        if(this.equals(player.getCurrentlyOpen())){
            ConsulatAPI.getNMS().getPacketNMS().setSlot(player.getPlayer(), slot, item);
        }
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
    @Override
    public void moveItem(int from, int to){
        moveItem(from, this, to);
    }
    
    @Override
    public void moveItem(int from, @NotNull IGui guiTo, int to){
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
    @Override
    @Nullable
    public GuiItem getItem(int slot){
        if(slot < 0){
            return null;
        }
        return items[slot];
    }
    
    /**
     * Ouvre ce gui au joueur visé
     *
     * @param player le joueur visé
     */
    @Override
    public void open(@NotNull ConsulatPlayer player){
        onOpen(new GuiOpenEvent(player));
        player.getPlayer().openInventory(inventory);
        player.setCurrentlyOpen(this);
    }
    
    /**
     * Mets à jour le slot visé
     *
     * @param slot le slot visé
     */
    public void update(int slot){
        GuiItem item = getItem(slot);
        inventory.setItem(slot, item);
    }
    
    @Override
    @NotNull
    public String getName(){
        return name;
    }
    
    /**
     * Changer le nom du gui
     *
     * @param name Le nouveau nom
     */
    @Override
    public void setName(String name){
        this.name = name;
        setTitle();
    }

    @Override
    public String buildInventoryTitle(){
        return name;
    }
    
    @Override
    public void setTitle(){
        String title = buildInventoryTitle();
        try {
            titleField.set(inventoryField.get(inventory), title);
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Supprime l'item au slot visé
     *
     * @param slot le slot visé
     */
    @Override
    public void removeItem(int slot){
        inventory.setItem(slot, null);
        items[slot] = null;
        this.update(slot);
    }
    
    @Override
    @NotNull
    public Inventory getInventory(){
        return inventory;
    }
    
    @Override
    @NotNull
    public List<GuiItem> getItems(){
        return Collections.unmodifiableList(Arrays.asList(this.items));
    }
    
    @Override
    public void onCreate(){
    
    }
    
    @Override
    public void onOpen(GuiOpenEvent event){
    
    }
    
    @Override
    public void onClose(GuiCloseEvent event){
    
    }
    
    @Override
    public void onClick(GuiClickEvent event){
    
    }
    
    @Override
    public void onRemove(GuiRemoveEvent event){
    
    }
    
    @Override
    public boolean isModifiable(){
        return (modifiers & 1) == 1;
    }
    
    @Override
    public void setModifiable(boolean modifiable){
        if(modifiable){
            modifiers |= 1;
        } else {
            modifiers &= Integer.MAX_VALUE - 1;
        }
    }
    
    @Override
    public boolean isDestroyOnClose(){
        return (modifiers & 2) == 2;
    }
    
    @Override
    public void setDestroyOnClose(boolean destroyOnClose){
        if(destroyOnClose){
            modifiers |= 2;
        } else {
            modifiers &= Integer.MAX_VALUE - 2;
        }
    }
    
    @Override
    public boolean isBackButton(){
        return (modifiers & 4) == 4;
    }
    
    @Override
    public void setBackButton(boolean backButton){
        if(backButton){
            modifiers |= 4;
        } else {
            modifiers &= Integer.MAX_VALUE - 4;
        }
    }
    
    @Override
    public String toString(){
        return "PagedGui{" +
                "name='" + name + '\'' +
                ", inventory=" + inventory +
                ", items=" + Arrays.toString(items) +
                '}';
    }
    
    @Override
    protected void finalize(){
        System.out.println("Deleting " + name);
    }
}