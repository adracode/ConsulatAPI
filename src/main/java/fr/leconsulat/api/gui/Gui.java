package fr.leconsulat.api.gui;

import fr.leconsulat.api.gui.events.GuiRemoveEvent;
import fr.leconsulat.api.gui.events.PagedGuiCreateEvent;
import fr.leconsulat.api.gui.events.PagedGuiRemoveEvent;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

//TODO: on destroy
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Gui<T> implements Iterable<GuiItem> {
    
    @Nullable private T data;
    @NotNull private List<PagedGui<T>> guis;
    @NotNull private GuiListener<T> listener;
    @Nullable private Gui<?> father;
    @NotNull private Map<Object, Gui<?>> children = Collections.emptyMap();
    @NotNull private Map<Object, Supplier<Gui<?>>> createChildren = Collections.emptyMap();
    
    private byte currentIndex = -1;
    
    protected Gui(@NotNull GuiListener<T> listener){
        this.listener = listener;
        this.data = null;
        this.guis = Collections.emptyList();
    }
    
    public Gui(@NotNull GuiListener<T> listener, @NotNull T data){
        this.listener = listener;
        this.data = data;
        this.guis = new ArrayList<>();
    }
    
    public int getItemsNumber(){
        return currentIndex + 1 + getCurrentPage() * listener.getLengthMoveableSlot();
    }
    
    @NotNull
    public Gui<T> createSimilar(T key){
        return getListener().createGui(key);
    }
    
    @SuppressWarnings("unchecked")
    @NotNull
    public Gui<T> getSimilar(T key){
        Gui<?> pagedGui = getFather().getChild(key);
        if(pagedGui == null){
            pagedGui = createSimilar(key);
            getFather().addChild(key, pagedGui);
        }
        return (Gui<T>)pagedGui;
    }
    
    @NotNull
    public <A> Gui<A> addChild(@Nullable Object key, @NotNull Gui<A> gui){
        if(children == Collections.EMPTY_MAP){
            children = new HashMap<>(1);
        }
        gui.setFather(this);
        children.put(key, gui);
        return gui;
    }
    
    @SuppressWarnings("unchecked")
    @Nullable
    public <A> Gui<A> getChild(@Nullable Object key){
        Gui<A> child = getLegacyChild(key);
        if(createChildren != Collections.EMPTY_MAP && (children == Collections.EMPTY_MAP || child == null)){
            Supplier<Gui<?>> supplier = createChildren.get(key);
            if(supplier != null){
                addChild(key, child = (Gui<A>)supplier.get());
            }
        }
        return child;
    }
    
    @SuppressWarnings("unchecked")
    @Nullable
    public <A> Gui<A> getLegacyChild(@Nullable Object key){
        if(children == Collections.EMPTY_MAP){
            return null;
        }
        return (Gui<A>)children.get(key);
    }
    
    public void prepareChild(@Nullable Object key, @NotNull Supplier<Gui<?>> supplier){
        if(createChildren == Collections.EMPTY_MAP){
            createChildren = new HashMap<>();
        }
        createChildren.put(key, supplier);
    }
    
    public boolean removeChild(Object key){
        if(children == Collections.EMPTY_MAP){
            return false;
        }
        Gui<?> removed = children.remove(key);
        if(removed != null){
            removed.remove();
        }
        return removed != null;
    }
    
    public boolean removeChild(Gui<?> gui){
        if(children == Collections.EMPTY_MAP){
            return false;
        }
        Object key = null;
        for(Map.Entry<Object, Gui<?>> entry : children.entrySet()){
            if(entry.getValue().equals(gui)){
                key = entry.getKey();
                break;
            }
        }
        Gui<?> removed = children.remove(key);
        if(removed != null){
            removed.remove();
        }
        return removed != null;
    }
    
    @NotNull
    public String getName(){
        return getPage().getName();
    }
    
    public boolean hasFather(){
        return father != null;
    }
    
    @SuppressWarnings("unchecked")
    @NotNull
    public <F> Gui<F> getFather(){
        if(father == null){
            throw new NullPointerException("Father of " + listener.getClass());
        }
        return (Gui<F>)father;
    }
    
    public void setFather(Gui<?> father){
        this.father = father;
        for(PagedGui<T> gui : guis){
            gui.setTitle();
        }
    }
    
    public int getCurrentPage(){
        return guis.size() - 1;
    }
    
    public int numberOfPages(){
        return guis.size();
    }
    
    public PagedGui<T> getPage(){
        return getPage(0);
    }
    
    public PagedGui<T> getPage(int page){
        PagedGui<T> gui = guis.get(page);
        if(gui == null && listener.isAutoCreatePage()){
            newPage();
        }
        return gui;
    }
    
    public List<PagedGui<T>> getPagedGuis(){
        return Collections.unmodifiableList(guis);
    }
    
    @NotNull
    public PagedGui<T> newPage(){
        PagedGui<T> template = listener.getTemplate();
        PagedGui<T> gui = template.copy();
        addPage(gui);
        gui.setTitle();
        int page = getCurrentPage();
        PagedGuiCreateEvent<T> event = new PagedGuiCreateEvent<>(gui, getData(), page);
        listener.onPageCreate(event);
        if(event.isCancelled()){
            removePage(page);
        }
        return gui;
    }
    
    public void addPage(PagedGui<T> gui){
        currentIndex = -1;
        guis.add(gui);
        int page = getCurrentPage();
        gui.setGui(this);
        gui.setPage(page);
    }
    
    public void addItem(GuiItem item){
        int length = listener.getLengthMoveableSlot();
        PagedGui<T> gui;
        if(++currentIndex >= length){
            gui = newPage();
            ++currentIndex;
        } else {
            gui = getPage(getCurrentPage());
        }
        gui.setItem(getCurrentSlot(), item);
    }
    
    public byte getCurrentSlot(){
        return getCurrentSlot(currentIndex);
    }
    
    public byte getCurrentSlot(byte slot){
        return listener.getMoveableSlot(slot);
    }
    
    public void removeItem(int page, int slot){
        PagedGui<T> gui = getPage(page);
        gui.removeItem(slot);
        int currentPage = getCurrentPage();
        byte currentSlot = getCurrentSlot();
        if(slot != currentSlot || currentPage != page){
            PagedGui<T> lastGui = getPage(currentPage);
            lastGui.moveItem(currentSlot, gui, slot);
        }
        if(currentIndex <= 0){
            if(currentPage > 0){
                removePage(currentPage);
            } else {
                currentIndex = -1;
            }
        } else {
            --currentIndex;
        }
    }
    
    public void removeAll(){
        for(Iterator<GuiItem> iterator = this.iterator(); iterator.hasNext(); ){
            iterator.remove();
        }
    }
    
    public boolean removePage(int page){
        PagedGui<T> removed = guis.remove(page);
        if(removed == null){
            return false;
        }
        if(page != 0){
            PagedGui<T> previous = getPage(page - 1);
            for(HumanEntity human : new ArrayList<>(removed.getInventory().getViewers())){
                previous.open(CPlayerManager.getInstance().getConsulatPlayer(human.getUniqueId()));
            }
        }
        PagedGuiRemoveEvent<T> event = new PagedGuiRemoveEvent<>(removed, getData(), page);
        listener.onRemove(event);
        currentIndex = (byte)(listener.getLengthMoveableSlot() - 1);
        return true;
    }
    
    @NotNull
    public T getData(){
        if(this.data == null){
            throw new IllegalStateException();
        }
        return this.data;
    }
    
    @NotNull
    @Override
    public Iterator<GuiItem> iterator(){
        return new GuiIterator();
    }
    
    @NotNull
    public GuiListener<T> getListener(){
        return listener;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Gui<?> gui = (Gui<?>)o;
        return Objects.equals(data, gui.data) &&
                listener.equals(gui.listener) &&
                Objects.equals(father, gui.father);
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(data, listener, father);
    }
    
    public Collection<Gui<?>> getChildren(){
        return Collections.unmodifiableCollection(children.values());
    }
    
    public void open(ConsulatPlayer player){
        open(player, 0);
    }
    
    public void open(ConsulatPlayer player, int page){
        getPage(page).open(player);
    }
    
    public void remove(){
        GuiRemoveEvent<T> event = new GuiRemoveEvent<>(this);
        getListener().onRemove(event);
        getListener().remove(this);
        for(Iterator<Map.Entry<Object, Gui<?>>> iterator = children.entrySet().iterator(); iterator.hasNext(); ){
            iterator.next().getValue().remove();
            iterator.remove();
        }
    }
    
    private class GuiIterator implements Iterator<GuiItem> {
        
        private int page = 0;
        private byte indexSlot = -1;
        
        @Override
        public boolean hasNext(){
            if(++indexSlot >= listener.getLengthMoveableSlot()){
                indexSlot = 0;
                ++page;
            }
            return page < getCurrentPage() || indexSlot <= currentIndex;
        }
        
        @Override
        public GuiItem next(){
            return getPage(page).getItem(getCurrentSlot(indexSlot));
        }
        
        @Override
        public void remove(){
            removeItem(page, getCurrentSlot(indexSlot));
        }
    }
    
    @Override
    protected void finalize(){
        System.out.println("Deleting paged " + getName());
    }
}
