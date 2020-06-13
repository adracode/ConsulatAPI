package fr.leconsulat.api.gui;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.gui.events.*;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Cette classe permet de créer des guis en l'étendant à une autre classe
 * Les listener dont représentés comme des arbres
 * L'UUID est l'identifiant du listener
 * Le type est le type de clé utilisé. On n'utilise pas un type paramétré puisqu'on a besoin de connaître le type, ce qui n'est
 * pas possible avec un type paramétré
 * La Map guis enregistre les différents guis crées, dont la clé est un objet type.
 * Father est le père du listener
 * Children sont les enfants du listener, dont la clé est le slot
 * Modifiable indique si on peux modifier certains items du Gui, comme en ajouter ou en retirer
 * Unique indique si le listener ne contient qu'un seul Gui
 * CreateOnOpen indique si un gui doit se créer s'il n'existe pas lorsqu'on essaie de l'ouvrir
 * <p>
 * On implémente un gui par défaut dans un listener dont la clé est null pour avoir une base pour de futures gui
 */
public abstract class GuiListener<T> implements Comparable<GuiListener<?>> {
    
    /* Identifiant du listener pour comparer facilement */
    private UUID uuid;
    /* Type de clé utilisé pour identifier les différents guis
     * (ex: si le type est ConsulatPlayer.class, on aura un gui par joueur) */
    private Class<T> type = null;
    private final byte line;
    private PagedGui<T> template;
    
    private short modifiers = 0;
    
    private boolean modifiable = false;
    private boolean unique = true;
    private boolean createOnOpen = true;
    private boolean destroyOnClose = false;
    private boolean autoCreatePage = true;
    private byte[] moveableItems;
    
    public GuiListener(int line){
        this(null, line);
    }
    
    public GuiListener(Class<T> type, int line){
        this.line = (byte)line;
        this.uuid = UUID.randomUUID();
        if(type != null){
            this.type = type;
            this.setUnique(false);
        }
    }
    
    public byte getMoveableSlot(int index){
        return moveableItems[index];
    }
    
    public byte getMaxMoveableSlot(){
        return moveableItems[getLengthMoveableSlot() - 1];
    }
    
    public int getLengthMoveableSlot(){
        return moveableItems.length;
    }
    
    /**
     * Ajoute un nouveau gui au listener
     *
     * @param listener le listener du gui
     * @param name     le nom affiché en haut du gui
     * @param items    les différents items du gui
     * @return le gui ajouté
     */
    public Gui<T> setTemplate(GuiListener<T> listener, String name, GuiItem... items){
        Gui<T> gui = new Gui<>(null, listener, name, items);
        template = new PagedGui<T>(GuiListener.this) {
            //@formatter:off
            @Override public void addGui(Gui<T> gui1){ throw new UnsupportedOperationException("Can't add page to template"); }
            @Override public void addItem(GuiItem item){ throw new UnsupportedOperationException("Can't add item to template"); }
            @Override public void removeItem(int page, int slot){ throw new UnsupportedOperationException("Can't remove item from template"); }
            @Override public boolean removeGui(int page){ throw new UnsupportedOperationException("Can't remove gui from template"); }
            @Override public Gui<T> getGui(int page){ return gui; }
            //@formatter:on
        };
        return gui;
    }
    
    /**
     * Créer un nouveau gui avec une nouvelle clé. Le gui est copié du gui par défaut
     *
     * @param key la nouvelle clé
     * @return la nouvelle page de gui créée, ou null si l'event a été annulé
     */
    @NotNull
    public PagedGui<T> create(T key){
        return create(key, null);
    }
    
    @Override
    public String toString(){
        return "GuiListener{" +
                "uuid=" + uuid +
                ", type=" + type +
                ", line=" + line +
                ", modifiable=" + modifiable +
                ", unique=" + unique +
                ", createOnOpen=" + createOnOpen +
                ", destroyOnClose=" + destroyOnClose +
                ", autoCreatePage=" + autoCreatePage +
                ", moveableItems=" + Arrays.toString(moveableItems) +
                '}';
    }
    
    @NotNull
    public PagedGui<T> create(T key, Gui<?> father, GuiItem... items){
        Gui<T> template = getTemplate();
        Gui<T> gui = template.copy();
        gui.setKey(key);
        if(father != null){
            gui.setFather(father);
        }
        for(GuiItem item : items){
            gui.setItem(item);
        }
        PagedGui<T> pagedGui;
        pagedGui = new PagedGui<>(this);
        pagedGui.addGui(gui);
        int page = pagedGui.getCurrentPage();
        PagedGuiCreateEvent<T> mainCreate = new PagedGuiCreateEvent<>(key, pagedGui);
        onCreateMain(mainCreate);
        if(mainCreate.isCancelled()){
            return pagedGui;
        }
        GuiCreateEvent<T> event = new GuiCreateEvent<>(gui, key, pagedGui, page);
        onCreate(event);
        if(event.isCancelled()){
            pagedGui.removeGui(page);
        }
        return pagedGui;
    }
    
    /**
     * Supprime un gui du listener
     *
     * @param key la clé correspondante au gui
     */
    public boolean removeGui(T key){
        return guis.remove(key) != null;
    }
    
    public boolean removePage(T key, int page){
        PagedGui<T> gui = this.guis.get(key);
        if(gui == null){
            return false;
        }
        return gui.removeGui(page);
    }
    
    @Nullable
    public PagedGui<T> getPagedGui(T key){
        PagedGui<T> pagedGui = guis.get(key);
        return pagedGui == null && isAutoCreatePage() ? create(key) : pagedGui;
    }
    
    /**
     * Renvoie le Gui par défaut
     *
     * @return le Gui par défaut
     */
    @NotNull
    public Gui<T> getTemplate(){
        Gui<T> template = getGui(null);
        if(template == null){
            throw new IllegalStateException("No template found");
        }
        return template;
    }
    
    @Nullable
    public Gui<T> getGui(T key){
        return getGui(key, 0);
    }
    
    @Nullable
    public Gui<T> getGui(@Nullable T key, int page){
        PagedGui<T> pagedGui = guis.get(key);
        if(pagedGui == null){
            return null;
        }
        if(key != null && page == pagedGui.pages() && isAutoCreatePage()){
            create(key);
        }
        //IndexOutOfBoundException est voulue si la page est incorrecte
        return pagedGui.getGui(page);
    }
    
    /**
     * Créer et renvoie un nouvel item
     *
     * @param name        Le nom à afficher
     * @param slot        Le slot où l'item sera placé
     * @param material    Le type de l'item
     * @param description La description à afficher
     * @return L'item nouvellement crée
     */
    public GuiItem getItem(String name, int slot, Material material, String... description){
        return new GuiItem(name, (byte)slot, material, Arrays.asList(description));
    }
    
    /**
     * Créer et renvoie une nouvelle tête
     *
     * @param name        Le nom à afficher
     * @param slot        Le slot où l'item sera placé
     * @param player      Le nom du skin de la tête
     * @param description La description à afficher
     * @return La tête nouvellement créée
     */
    public GuiItem getItem(String name, int slot, String player, String... description){
        return new GuiItem(name, (byte)slot, player, Arrays.asList(description));
    }
    
    public GuiItem getItem(String name, int slot, UUID player, String... description){
        return new GuiItem(name, (byte)slot, player, Arrays.asList(description));
    }
    
    public GuiItem getItem(GuiItem item, int slot){
        return item.clone().setSlot(slot);
    }
    
    public boolean open(ConsulatPlayer player, T key){
        return open(player, key, 0);
    }
    
    public boolean open(ConsulatPlayer player, T key, Gui<?> father){
        return open(player, key, father, 0);
    }
    
    public boolean open(ConsulatPlayer player, T key, int page){
        return open(player, key, null, page);
    }
    
    /**
     * Ouvre un gui à un joueur
     *
     * @param player le joueur qui doit ouvrir le gui
     * @param key    la clé correspondante au gui à ouvrir
     * @return true si le gui a été ouvert
     */
    public boolean open(ConsulatPlayer player, T key, Gui<?> father, int page){
        Gui<T> gui = getGui(key, page);
        if(gui == null){
            if(isCreateOnOpen()){
                gui = create(key, father).getGui(page);
            } else {
                return false;
            }
        }
        GuiOpenEvent<T> event = new GuiOpenEvent<>(player, gui, key);
        onOpen(event);
        if(event.isCancelled()){
            return false;
        }
        gui.open(player);
        return true;
    }
    
    void close(GuiCloseEvent<T> e){
        onClose(e);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () -> {
            if(e.isCancelled()){
                open(e.getPlayer(), e.getKey(), e.getGui().getPage());
                return;
            }
            if(e.getPlayer().getPlayer().getOpenInventory().getTitle().equals("Crafting")){
                e.getPlayer().setCurrentlyOpen(null);
                if(e.getFather() != null && e.isOpenFatherGui()){
                    //noinspection unchecked
                    e.getFather().getListener().open(e.getPlayer(), e.getFather().getKey());
                }
            }
        }, 1L);
        if(isDestroyOnClose()){
            removeGui(e.getKey());
        }
    }
    
    public void setMoveableItemsRange(int from, int to){
        if(from < 0){
            throw new IllegalArgumentException("A slot cannot be below 0");
        }
        if(to > line * 9 - 1){
            throw new IllegalArgumentException("A slot cannot be above the maximum slot ");
        }
        if(from > to){
            throw new IllegalArgumentException("To must be higher than from");
        }
        this.moveableItems = new byte[to - from];
        for(byte i = (byte)from; i < to; i++){
            moveableItems[i - from] = i;
        }
    }
    
    public void setMoveableItems(int... slots){
        Set<Byte> sorted = new TreeSet<>();
        for(int slot : slots){
            sorted.add((byte)slot);
        }
        if(slots[0] < 0){
            throw new IllegalArgumentException("A slot cannot be below 0");
        }
        if(slots[slots.length - 1] > line * 9 - 1){
            throw new IllegalArgumentException("A slot cannot be above the maximum slot ");
        }
        this.moveableItems = new byte[slots.length];
        int i = -1;
        for(byte slot : sorted){
            this.moveableItems[++i] = slot;
        }
    }
    
    protected Map<Object, PagedGui<T>> getGuis(){
        return Collections.unmodifiableMap(guis);
    }
    
    public int getPages(T key){
        PagedGui<T> guis = this.guis.get(key);
        return guis == null ? 0 : guis.pages();
    }
    
    public boolean isModifiable(){
        return modifiable;
    }
    
    public void setModifiable(boolean modifiable){
        this.modifiable = modifiable;
    }
    
    public boolean isUnique(){
        return unique;
    }
    
    public void setUnique(boolean unique){
        this.unique = unique;
    }
    
    public boolean isCreateOnOpen(){
        return createOnOpen;
    }
    
    public void setCreateOnOpen(boolean createOnOpen){
        this.createOnOpen = createOnOpen;
    }
    
    public void onCreateMain(PagedGuiCreateEvent<T> event){
    }
    
    public void onCreate(GuiCreateEvent<T> event){
    }
    
    public void onOpen(GuiOpenEvent<T> event){
    }
    
    public void onClose(GuiCloseEvent<T> event){
    }
    
    public void onClick(GuiClickEvent<T> event){
    }
    
    public Class<?> getType(){
        return type;
    }
    
    protected static final GuiItem highLess = new GuiItem("§4-", (byte)0, Material.RED_CONCRETE);
    protected static final GuiItem less = new GuiItem("§c", (byte)1, Material.ORANGE_CONCRETE);
    protected static final GuiItem fewLess = new GuiItem("§6", (byte)2, Material.YELLOW_CONCRETE);
    protected static final GuiItem fewMore = new GuiItem("§b", (byte)6, Material.LIME_CONCRETE);
    protected static final GuiItem more = new GuiItem("§a", (byte)7, Material.GREEN_CONCRETE);
    protected static final GuiItem highMore = new GuiItem("§2", (byte)8, Material.BLUE_CONCRETE);
    protected static final GuiItem activate = new GuiItem("§2Activer", (byte)13, Material.GREEN_CONCRETE);
    protected static final GuiItem deactivate = new GuiItem("§4Désactiver", (byte)14, Material.RED_CONCRETE);
    protected static final GuiItem validate = new GuiItem("§aValider", (byte)0, Material.LIME_DYE);
    protected static final GuiItem cancel = new GuiItem("§cAnnuler", (byte)0, Material.RED_DYE);
    protected static final GuiItem back = new GuiItem("§cRetour", (byte)0, Material.RED_STAINED_GLASS_PANE);
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof GuiListener)) return false;
        GuiListener<?> listener = (GuiListener<?>)o;
        return uuid.equals(listener.uuid);
    }
    
    @Override
    public int hashCode(){
        return uuid.hashCode();
    }
    
    @Override
    public int compareTo(GuiListener o){
        return uuid.compareTo(o.uuid);
    }
    
    public boolean isAutoCreatePage(){
        return autoCreatePage;
    }
    
    public void setAutoCreatePage(boolean autoCreatePage){
        this.autoCreatePage = autoCreatePage;
    }
    
    public byte getLine(){
        return line;
    }
    
    public boolean isDestroyOnClose(){
        return destroyOnClose;
    }
    
    public void setDestroyOnClose(boolean destroyOnClose){
        this.destroyOnClose = destroyOnClose;
    }
    
    public void open(ConsulatPlayer player){
        open(player, null);
    }
    
    public boolean isNoGuisStocked(){
        return noGuisStocked;
    }
    
    public void setNoGuisStocked(boolean noGuisStocked){
        this.noGuisStocked = noGuisStocked;
        if(noGuisStocked){
            this.guis = null;
        }
    }
}
