package fr.leconsulat.api.gui;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.gui.events.GuiOpenEvent;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Cette classe est un PagedGui, c'est à dire une page d'un Gui
 * Un gui peut avoir une ou plusieurs pages.
 *
 * @param <T> Ce paramètre sert à spécifier le type
 *            de donnée utilisé
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PagedGui<T> implements IGui {
    
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
    
    //Titre affiché dans l'inventaire
    @NotNull private String name;
    //Numéro de page
    private int page = 0;
    //Le Gui utilisant le PagedGui
    @NotNull private Gui<T> gui;
    //Inventaire natif de Minecraft
    @NotNull private Inventory inventory;
    //Les différents items contenant dans le gui
    private GuiItem[] items;
    
    /**
     * Créer un PagedGui
     *
     * @param gui Le Gui auquel le PagedGui sera attribué
     * @param name Le nom affiché dans le Gui
     * @param items Les différents items composant le Gui
     */
    public PagedGui(@NotNull Gui<T> gui, @NotNull String name, GuiItem... items){
        this.gui = gui;
        GuiListener<T> listener = getListener();
        this.name = name;
        this.items = new GuiItem[listener.getLine() * 9];
        this.inventory = Bukkit.createInventory(this, listener.getLine() * 9, buildInventoryTitle());
        for(GuiItem item : items){
            setItem(item);
        }
    }
    
    /**
     * Constructeur par copie
     * @param pagedGui Le PagedGui à copier
     */
    private PagedGui(@NotNull PagedGui<T> pagedGui){
        this.name = pagedGui.name;
        this.page = pagedGui.page;
        this.gui = pagedGui.gui;
        GuiListener<T> listener = getListener();
        this.items = new GuiItem[listener.getLine() * 9];
        this.inventory = Bukkit.createInventory(this, listener.getLine() * 9, buildInventoryTitle());
        for(GuiItem item : pagedGui.items){
            if(item != null){
                this.setItem(item.clone());
            }
        }
    }
    
    /**
     * Réalise une copie
     * @return Le Gui copié
     */
    @NotNull
    public PagedGui<T> copy(){
        return new PagedGui<>(this);
    }
    
    @NotNull
    public Gui<T> getGui(){
        return gui;
    }
    
    void setGui(@NotNull Gui<T> gui){
        this.gui = gui;
    }
    
    /**
     * Place des items de "décoration" dans le Gui.
     * Un tel item n'aura pas de nom
     * @param type Le type de l'item
     * @param slots Les slots où placer les items
     * @return this
     */
    @NotNull
    public PagedGui<T> setDeco(@NotNull Material type, int... slots){
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
    public PagedGui<T> setItem(@NotNull GuiItem item){
        return setItem(item.getSlot(), item);
    }
    
    @NotNull
    public PagedGui<T> setItem(int slot, @Nullable GuiItem item){
        if(item != null){
            item.setSlot(slot);
        }
        inventory.setItem(slot, item);
        items[slot] = item;
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
    
    public void moveItem(int from, @NotNull PagedGui<T> guiTo, int to){
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
    @Nullable
    public GuiItem getItem(int slot){
        return items[slot];
    }
    
    @NotNull
    public GuiListener<T> getListener(){
        return gui.getListener();
    }
    
    @Override
    public PagedGui<T> getPagedGui(){
        return this;
    }
    
    @Override
    public Gui<?> getFather(){
        return getGui().hasFather() ? getGui().getFather() : null;
    }
    
    /**
     * Ouvre ce gui au joueur visé
     *
     * @param player le joueur visé
     */
    public void open(@NotNull ConsulatPlayer player){
        GuiOpenEvent<T> event = new GuiOpenEvent<>(player, this, getGui().getData());
        getListener().onOpen(event);
        if(event.isCancelled()){
            return;
        }
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
    
    @NotNull
    public String getName(){
        return name;
    }
    
    /**
     * Changer le nom du gui
     *
     * @param name Le nouveau nom
     */
    public void setName(String name){
        this.name = name;
        setTitle();
        for(Gui<?> child : getGui().getChildren()){
            for(PagedGui<?> page : child.getPagedGuis()){
                page.setTitle();
            }
        }
    }
    
    private String buildInventoryTitle(){
        if(!gui.hasFather()){
            return name;
        } else {
            return gui.getFather().getName() + " > " + name;
        }
    }
    
    void setTitle(){
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
    public void removeItem(int slot){
        inventory.setItem(slot, null);
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
        PagedGui<?> gui = (PagedGui<?>)o;
        return this.gui.equals(gui.gui) &&
                page == gui.page;
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(gui, page);
    }
    
    @Override
    @NotNull
    public Inventory getInventory(){
        return inventory;
    }
    
    @NotNull
    public List<GuiItem> getItems(){
        return Collections.unmodifiableList(Arrays.asList(this.items));
    }
    
    @Override
    public String toString(){
        return "PagedGui{" +
                "name='" + name + '\'' +
                ", page=" + page +
                ", gui=" + gui +
                ", inventory=" + inventory +
                ", items=" + Arrays.toString(items) +
                '}';
    }
    
    @Override
    protected void finalize(){
        System.out.println("Deleting " + name);
    }
}