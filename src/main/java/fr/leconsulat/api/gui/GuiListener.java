package fr.leconsulat.api.gui;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.gui.events.GuiClickEvent;
import fr.leconsulat.api.gui.events.GuiCloseEvent;
import fr.leconsulat.api.gui.events.GuiCreateEvent;
import fr.leconsulat.api.gui.events.GuiOpenEvent;
import fr.leconsulat.api.player.CPlayerManager;
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
 * AutoCreate indique si le gui doit se créer à la connexion du joueur dans le cas d'un type ConsulatPlayer.class
 * <p>
 * On implémente un gui par défaut dans un listener dont la clé est null pour avoir une base pour de futures gui
 */
public abstract class GuiListener implements Comparable<GuiListener> {
    
    private UUID uuid;
    private Class<?> type = null;
    private final Map<Object, PagedGui> guis = new HashMap<>();
    
    private final GuiListener father;
    private final Map<Byte, GuiListener> children = new HashMap<>();
    private final byte line;
    
    private boolean modifiable = false;
    private boolean unique = true;
    private boolean createOnOpen = false;
    private boolean autoCreatePage = true;
    private boolean autoCreate = true; //In case of player key
    private byte[] moveableItems;
    
    public GuiListener(GuiListener father, int line){
        this(father, null, line);
    }
    
    public GuiListener(GuiListener father, Class<?> type, int line){
        this.line = (byte)line;
        this.uuid = UUID.randomUUID();
        this.father = father;
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
    public Gui setTemplate(GuiListener listener, String name, GuiItem... items){
        Gui gui = new Gui(null, listener, name, items);
        PagedGui pagedGui = new PagedGui(this) {
            //@formatter:off
            @Override public void addGui(Gui gui){ throw new UnsupportedOperationException("Can't add page to template"); }
            @Override public void addItem(GuiItem item){ throw new UnsupportedOperationException("Can't add item to template"); }
            @Override public void removeItem(int page, int slot){ throw new UnsupportedOperationException("Can't remove item from template"); }
            @Override public boolean removeGui(int page){ throw new UnsupportedOperationException("Can't remove gui from template"); }
            @Override public Gui getGui(int page){ return gui; }
            //@formatter:on
        };
        if(!isUnique() && (type.equals(ConsulatPlayer.class) || type.equals(CPlayerManager.getInstance().getPlayerClass()))){
            GuiManager.getInstance().setPlayerBounded(this);
        }
        this.guis.putIfAbsent(null, pagedGui);
        return gui;
    }
    
    /**
     * Créer un nouveau gui avec une nouvelle clé. Le gui est copié du gui par défaut
     *
     * @param key la nouvelle clé
     * @return la nouvelle page de gui créée, ou null si l'event a été annulé
     */
    @NotNull
    public PagedGui create(Object key){
        return create(key, null);
    }
    
    @NotNull
    public PagedGui create(Object key, Object fatherKey){
        return create(key, fatherKey, null);
    }
    
    @NotNull
    public PagedGui create(Object key, String name, GuiItem... items){
        return create(key, null, name, items);
    }
    
    @NotNull
    public PagedGui create(Object key, Object fatherKey, String name, GuiItem... items){
        Gui defaultGui = getTemplate();
        if(defaultGui == null){
            throw new IllegalStateException("Un gui par défaut doit être spécifié");
        }
        Gui gui = defaultGui.copy(key, name);
        for(GuiItem item : items){
            gui.setItem(item);
        }
        PagedGui pagedGui = guis.get(key);
        if(pagedGui == null){
            pagedGui = new PagedGui(this);
        }
        pagedGui.addGui(gui);
        if(fatherKey != null){
            gui.setFatherKey(fatherKey);
        }
        this.guis.putIfAbsent(key, pagedGui);
        GuiCreateEvent event = new GuiCreateEvent(gui, key, pagedGui, pagedGui.pages() - 1);
        onCreate(event);
        if(event.isCancelled()){
            this.guis.remove(key);
            pagedGui.removeGui(pagedGui.pages() - 1);
        }
        return pagedGui;
    }
    
    /**
     * Supprime un gui du listener
     *
     * @param key la clé correspondante au gui
     */
    public boolean removeGui(Object key){
        return guis.remove(key) != null;
    }
    
    public boolean removePage(Object key, int page){
        PagedGui gui = this.guis.get(key);
        if(gui == null){
            return false;
        }
        return gui.removeGui(page);
    }
    
    @Nullable
    public PagedGui getPagedGui(Object key){
        PagedGui pagedGui = guis.get(key);
        if(pagedGui == null && isAutoCreatePage()){
            return create(key);
        }
        return pagedGui;
    }
    
    /**
     * Renvoie le Gui par défaut
     *
     * @return le Gui par défaut
     */
    @Nullable
    public Gui getTemplate(){
        return getGui(null);
    }
    
    @Nullable
    public Gui getGui(Object key){
        return getGui(key, 0);
    }
    
    @Nullable
    public Gui getGui(@Nullable Object key, int page){
        PagedGui pagedGui = guis.get(key);
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
    
    public boolean open(ConsulatPlayer player, Object key){
        return open(player, key, 0);
    }
    
    /**
     * Ouvre un gui à un joueur
     *
     * @param player le joueur qui doit ouvrir le gui
     * @param key    la clé correspondante au gui à ouvrir
     * @return true si le gui a été ouvert
     */
    public boolean open(ConsulatPlayer player, Object key, int page){
        Gui gui = getGui(key, page);
        if(gui == null){
            if(isCreateOnOpen()){
                gui = create(key).getGui(page);
            } else {
                return false;
            }
        }
        GuiOpenEvent event = new GuiOpenEvent(player, gui, key);
        onOpen(event);
        if(event.isCancelled()){
            return false;
        }
        gui.open(player);
        return true;
    }
    
    void close(GuiCloseEvent e){
        onClose(e);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () -> {
            if(e.isCancelled()){
                open(e.getPlayer(), e.getKey(), e.getGui().getPage());
                return;
            }
            if(e.getPlayer().getPlayer().getOpenInventory().getTitle().equals("Crafting")){
                e.getPlayer().setCurrentlyOpen(null);
                if(getFather() != null && e.isOpenFatherGui()){
                    this.getFather().getGui(e.getFatherKey()).open(e.getPlayer());
                }
            }
        }, 1L);
    }
    
    /**
     * Ajouter un enfant au listener
     *
     * @param slot  le slot concerné
     * @param child le listener concerné
     */
    public void addChild(int slot, GuiListener child){
        this.children.put((byte)slot, child);
    }
    
    /**
     * Renvoie un enfant du listener
     *
     * @param slot le slot concerné par le listener
     * @return le listener
     */
    public GuiListener getChild(int slot){
        return children.get((byte)slot);
    }
    
    public void setMoveableItems(int from, int to){
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
    
    public void setMoveableItems(byte... slots){
        Set<Byte> sorted = new TreeSet<>();
        for(byte slot : slots){
            sorted.add(slot);
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
    
    protected Map<Object, PagedGui> getGuis(){
        return Collections.unmodifiableMap(guis);
    }
    
    public int getPages(Object key){
        PagedGui guis = this.guis.get(key);
        return guis == null ? 0 : guis.pages();
    }
    
    public GuiListener getFather(){
        return father;
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
    
    public boolean isAutoCreate(){
        return autoCreate;
    }
    
    public void setAutoCreate(boolean autoCreate){
        this.autoCreate = autoCreate;
    }
    
    public abstract void onCreate(GuiCreateEvent event);
    
    public abstract void onOpen(GuiOpenEvent event);
    
    public abstract void onClose(GuiCloseEvent event);
    
    public abstract void onClick(GuiClickEvent event);
    
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
    public String toString(){
        return "AGListener{" +
                "uuid=" + uuid +
                ", type=" + type +
                ", guis=" + guis +
                ", modifiable=" + modifiable +
                ", unique=" + unique +
                '}';
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof GuiListener)) return false;
        GuiListener listener = (GuiListener)o;
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
}
