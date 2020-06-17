package fr.leconsulat.api.gui;

import fr.leconsulat.api.gui.events.*;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

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
    private final byte line;
    private Template template;
    
    private short modifiers = 0;
    
    private boolean modifiable = false;
    private boolean unique = true;
    private boolean createOnOpen = true;
    private boolean destroyOnClose = false;
    private boolean autoCreatePage = true;
    private byte[] moveableItems;
    
    public GuiListener(int line){
        this.line = (byte)line;
        this.uuid = UUID.randomUUID();
    }
    
    public final byte getMoveableSlot(int index){
        return moveableItems[index];
    }
    
    public final byte getMaxMoveableSlot(){
        return moveableItems[getLengthMoveableSlot() - 1];
    }
    
    public final int getLengthMoveableSlot(){
        return moveableItems.length;
    }
    
    /**
     * Ajoute un nouveau gui au listener
     *
     * @param name  le nom affiché en haut du gui
     * @param items les différents items du gui
     * @return le gui ajouté
     */
    public final PagedGui<T> setTemplate(String name, GuiItem... items){
        return (template = new Template(name, items)).getPage();
    }
    
    /**
     * Créer un nouveau gui avec une nouvelle clé. Le gui est copié du gui par défaut
     *
     * @param key la nouvelle clé
     * @return la nouvelle page de gui créée, ou null si l'event a été annulé
     */
    @NotNull
    public final Gui<T> createGui(T key){
        return createGui(key, null);
    }
    
    @Override
    public final String toString(){
        return "GuiListener{" +
                "uuid=" + uuid +
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
    public final Gui<T> createGui(T key, Gui<?> father, GuiItem... items){
        PagedGui<T> template = getTemplate();
        PagedGui<T> gui = template.copy();
        for(GuiItem item : items){
            gui.setItem(item);
        }
        Gui<T> pagedGui;
        pagedGui = new Gui<>(this, key);
        pagedGui.addPage(gui);
        pagedGui.setFather(father);
        int page = pagedGui.getCurrentPage();
        GuiCreateEvent<T> mainCreate = new GuiCreateEvent<>(key, pagedGui);
        onCreate(mainCreate);
        PagedGuiCreateEvent<T> event = new PagedGuiCreateEvent<>(gui, key, page);
        onPageCreate(event);
        if(event.isCancelled()){
            pagedGui.removePage(page);
        }
        return pagedGui;
    }
    
    /**
     * Renvoie le Gui par défaut
     *
     * @return le Gui par défaut
     */
    @NotNull
    public final PagedGui<T> getTemplate(){
        return template.getPage();
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
    protected final GuiItem getItem(String name, int slot, Material material, String... description){
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
    protected final GuiItem getItem(String name, int slot, String player, String... description){
        return new GuiItem(name, (byte)slot, player, Arrays.asList(description));
    }
    
    protected final GuiItem getItem(String name, int slot, UUID player, String... description){
        return new GuiItem(name, (byte)slot, player, Arrays.asList(description));
    }
    
    protected final GuiItem getItem(GuiItem item, int slot){
        return item.clone().setSlot(slot);
    }
    
    protected final void setMoveableItemsRange(int from, int to){
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
    
    protected final void setMoveableItems(int... slots){
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
    
    public final boolean isModifiable(){
        return modifiable;
    }
    
    public final void setModifiable(boolean modifiable){
        this.modifiable = modifiable;
    }
    
    public final boolean isUnique(){
        return unique;
    }
    
    public final void setUnique(boolean unique){
        this.unique = unique;
    }
    
    public final boolean isCreateOnOpen(){
        return createOnOpen;
    }
    
    public final void setCreateOnOpen(boolean createOnOpen){
        this.createOnOpen = createOnOpen;
    }
    
    public void onCreate(GuiCreateEvent<T> event){
    }
    
    public void onPageCreate(PagedGuiCreateEvent<T> event){
    }
    
    public void onOpen(GuiOpenEvent<T> event){
    }
    
    public void onClose(GuiCloseEvent<T> event){
    }
    
    public void onClick(GuiClickEvent<T> event){
    }
    
    public void onRemove(PagedGuiRemoveEvent<T> event){
        System.out.println("Oui");
    }
    
    public void onRemove(GuiRemoveEvent<T> event){
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
    public final boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof GuiListener)) return false;
        GuiListener<?> listener = (GuiListener<?>)o;
        return uuid.equals(listener.uuid);
    }
    
    @Override
    public final int hashCode(){
        return uuid.hashCode();
    }
    
    @Override
    public final int compareTo(GuiListener o){
        return uuid.compareTo(o.uuid);
    }
    
    public final boolean isAutoCreatePage(){
        return autoCreatePage;
    }
    
    public final void setAutoCreatePage(boolean autoCreatePage){
        this.autoCreatePage = autoCreatePage;
    }
    
    public final byte getLine(){
        return line;
    }
    
    public final boolean isDestroyOnClose(){
        return destroyOnClose;
    }
    
    public final void setDestroyOnClose(boolean destroyOnClose){
        this.destroyOnClose = destroyOnClose;
    }
    
    private class Template extends Gui<T> {
        
        private PagedGui<T> template;
        
        public Template(String name, GuiItem... items){
            super(GuiListener.this);
            this.template = new PagedGui<>(this, name, items);
        }
    
        //@formatter:off
        @Override public int getCurrentPage(){ return 0; }
        @Override public int numberOfPages(){ return 1; }
        @Override public PagedGui<T> getPage(){ return this.template; }
        @Override public PagedGui<T> getPage(int page){ return getPage(); }
        @Override @NotNull public GuiListener<T> getListener(){ return GuiListener.this; }
        
        @Override @NotNull public PagedGui<T> newPage(){ throw new UnsupportedOperationException(); }
        @Override public void addPage(PagedGui<T> gui){ throw new UnsupportedOperationException(); }
        @Override public void addItem(GuiItem item){ throw new UnsupportedOperationException(); }
        @Override public byte getCurrentSlot(){ throw new UnsupportedOperationException(); }
        @Override public byte getCurrentSlot(byte slot){ throw new UnsupportedOperationException(); }
        @Override public void removeItem(int page, int slot){ throw new UnsupportedOperationException(); }
        @Override public boolean removePage(int page){ throw new UnsupportedOperationException(); }
        @Override @NotNull public T getData(){ throw new UnsupportedOperationException(); }
        @Override @NotNull public Iterator<GuiItem> iterator(){ throw new UnsupportedOperationException(); }
        //@formatter:on
    }
    
}
