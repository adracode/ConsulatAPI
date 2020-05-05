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
    private final Map<Object, Gui> guis = new HashMap<>();
    
    private final GuiListener father;
    private final Map<Byte, GuiListener> children = new HashMap<>();
    
    private boolean modifiable = false;
    private boolean unique = true;
    private boolean createOnOpen = false;
    private boolean autoCreate = true; //In case of player key
    
    public GuiListener(GuiListener father){
        this(father, null);
    }
    
    public GuiListener(GuiListener father, Class<?> type){
        this.uuid = UUID.randomUUID();
        this.father = father;
        if(type != null){
            this.type = type;
            this.setUnique(false);
        }
    }
    
    /**
     * Renvoie le Gui par défaut
     *
     * @return le Gui par défaut
     */
    public Gui getGui(){
        return getGui(null);
    }
    
    /**
     * Renvoie un gui spécifique
     *
     * @param key la clé correspondant au gui
     * @return le gui spécifique
     */
    public Gui getGui(Object key){
        return guis.get(key);
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
    
    /**
     * Ajoute un nouveau gui au listener
     *
     * @param key      la clé correspondant au gui
     * @param listener le listener du gui
     * @param name     le nom affiché en haut du gui
     * @param lines    le nombre de lignes du gui
     * @param items    les différents items du gui
     * @return le gui ajouté
     */
    public Gui addGui(Object key, GuiListener listener, String name, int lines, GuiItem... items){
        return addGui(key, new Gui(key, listener, name, lines, items));
    }
    
    /**
     * Ajoute un gui existant au listener
     *
     * @param key La clé correspondante au listener
     * @param gui le gui à ajouté
     * @return le gui ajouté
     */
    public Gui addGui(Object key, Gui gui){
        if(key == null && !isUnique() && (type.equals(ConsulatPlayer.class) || type.equals(CPlayerManager.getInstance().getPlayerClass()))){
            GuiManager.getInstance().setPlayerBounded(this);
        }
        this.guis.put(key, gui);
        gui.setKey(key);
        return gui;
    }
    
    /**
     * Supprime un gui du listener
     *
     * @param key la clé correspondante au gui
     */
    public void removeGui(Object key){
        guis.remove(key);
    }
    
    /**
     * Ouvre un gui à un joueur
     *
     * @param player le joueur qui doit ouvrir le gui
     * @param key    la clé correspondante au gui à ouvrir
     * @return true si le gui a été ouvert
     */
    public boolean open(ConsulatPlayer player, Object key){
        Gui gui = getGui(key);
        if(gui == null){
            if(isCreateOnOpen()){
                gui = create(key);
            } else {
                return false;
            }
        }
        GuiOpenEvent event = new GuiOpenEvent(player);
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
            if(e.getPlayer().getPlayer().getOpenInventory().getTitle().equals("Crafting")){
                e.getPlayer().setCurrentlyOpen(null);
                if(getFather() != null && e.isOpenFatherGui()){
                    this.getFather().open(e.getPlayer(), e.getKey());
                }
            }
        }, 1L);
        
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
    
    public GuiListener getFather(){
        return father;
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
    
    /**
     * Créer un nouveau gui avec une nouvelle clé. Le gui est copié du gui par défaut
     *
     * @param key la nouvelle clé
     * @return le nouveau gui crée
     */
    public Gui create(Object key){
        Gui defaultGui = getGui();
        if(defaultGui == null){
            throw new IllegalStateException("Un gui par défaut doit être spécifié");
        }
        Gui gui = defaultGui.copy(key);
        addGui(key, gui);
        return gui;
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
    
    protected Map<Object, Gui> getGuis(){
        return Collections.unmodifiableMap(guis);
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
}
