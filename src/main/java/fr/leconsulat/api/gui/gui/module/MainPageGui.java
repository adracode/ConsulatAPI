package fr.leconsulat.api.gui.gui.module;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.*;
import fr.leconsulat.api.gui.gui.BaseGui;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.MainPage;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MainPageGui implements MainPage {
    
    private final MainPage gui;
    
    private final List<Pageable> pages = new ArrayList<>();
    private byte currentIndex = -1;
    private byte[] dynamicItems;
    private GuiItem[] template;
    
    public MainPageGui(MainPage gui){
        this.gui = gui;
        List<GuiItem> items = new ArrayList<>();
        for(GuiItem item : gui.getItems()){
            if(item != null){
                items.add(new GuiItem(item));
            }
        }
        this.template = items.toArray(new GuiItem[0]);
        pages.add(this);
    }
    
    @Override
    public Pageable getPage(int page){
        if(page == pages.size()){
            return newPage();
        }
        return pages.get(page);
    }
    
    @Override
    public final void setDynamicItemsRange(int from, int to){
        if(from < 0){
            throw new IllegalArgumentException("A slot cannot be below 0");
        }
        if(to > getLine() * 9 - 1){
            throw new IllegalArgumentException("A slot cannot be above the maximum slot ");
        }
        if(from > to){
            throw new IllegalArgumentException("To must be higher than from");
        }
        this.dynamicItems = new byte[to - from];
        for(byte i = (byte)from; i < to; i++){
            dynamicItems[i - from] = i;
        }
    }
    
    @Override
    public final void setDynamicItems(int... slots){
        Set<Byte> sorted = new TreeSet<>();
        for(int slot : slots){
            sorted.add((byte)slot);
        }
        if(slots[0] < 0){
            throw new IllegalArgumentException("A slot cannot be below 0");
        }
        if(slots[slots.length - 1] > getLine() * 9 - 1){
            throw new IllegalArgumentException("A slot cannot be above the maximum slot ");
        }
        this.dynamicItems = new byte[slots.length];
        int i = -1;
        for(byte slot : sorted){
            this.dynamicItems[++i] = slot;
        }
    }
    
    @Override
    public void addPage(Pageable gui){
        currentIndex = -1;
        pages.add(gui);
        gui.setPage(getCurrentPage());
        gui.setMainPage(this);
    }
    
    @Override
    public Pageable createPage(){
        return new PageableGui(this, getName(), getLine(), template);
    }
    
    @NotNull
    public Pageable newPage(){
        Pageable pageGui = createPage();
        addPage(pageGui);
        gui.onPageCreated(new GuiCreateEvent(gui), pageGui);
        return pageGui;
    }
    
    @Override
    public void removePage(int page){
        IGui removed = pages.remove(page);
        if(removed == null){
            return;
        }
        if(page != 0){
            Pageable previous = getPage(page - 1);
            for(HumanEntity human : new ArrayList<>(removed.getInventory().getViewers())){
                previous.open(CPlayerManager.getInstance().getConsulatPlayer(human.getUniqueId()));
            }
        }
        GuiRemoveEvent event = new GuiRemoveEvent();
        onRemove(event);
        currentIndex = (byte)(dynamicItems.length - 1);
    }
    
    @Override
    public int numberOfPages(){
        return pages.size();
    }
    
    @Override
    public int getCurrentPage(){
        return numberOfPages() - 1;
    }
    
    @Override
    public final int getItemsNumber(){
        return currentIndex + 1 + getCurrentPage() * dynamicItems.length;
    }
    
    @Override
    public byte getCurrentSlot(){
        return dynamicItems[currentIndex];
    }
    
    @Override
    public void addItem(GuiItem item){
        int length = dynamicItems.length;
        Pageable gui;
        if(++currentIndex >= length){
            gui = newPage();
            ++currentIndex;
        } else {
            gui = getPage(getCurrentPage());
        }
        gui.setItem(getCurrentSlot(), item);
    }
    
    @Override
    public void removeItem(int page, int slot){
        Pageable gui = getPage(page);
        gui.removeItem(slot);
        int currentPage = getCurrentPage();
        byte currentSlot = getCurrentSlot();
        if(slot != currentSlot || currentPage != page){
            Pageable lastGui = getPage(currentPage);
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
    
    @Override
    public void removeAll(){
        for(Iterator<GuiItem> iterator = this.iterator(); iterator.hasNext(); ){
            iterator.remove();
        }
    }
    
    @Override
    public List<Pageable> getPages(){
        return Collections.unmodifiableList(pages);
    }
    
    @NotNull
    @Override
    public Iterator<GuiItem> iterator(){
        return new GuiIterator();
    }
    
    @Override
    public int getPage(){
        return 0;
    }
    
    @Override
    public void setPage(int page){
    }
    
    @Override
    public MainPage getMainPage(){
        return this;
    }
    
    @Override
    public void setMainPage(MainPage mainPage){
    }
    
    @Override
    public void onPageCreated(GuiCreateEvent event, Pageable pageGui){
        gui.onPageCreated(event, pageGui);
    }
    
    @Override
    public void onPageClick(GuiClickEvent event, Pageable pageGui){
        gui.onPageClick(event, pageGui);
    
    }
    
    @Override
    public void onPageOpen(GuiOpenEvent event, Pageable pageGui){
        gui.onPageOpen(event, pageGui);
    }
    
    @Override
    public void onPageOpened(GuiOpenEvent event, Pageable pageGui){
        gui.onPageOpened(event, pageGui);
    }
    
    @Override
    public void onPageClose(GuiCloseEvent event, Pageable pageGui){
        gui.onPageClose(event, pageGui);
    
    }
    
    @Override
    public void onPageRemoved(GuiRemoveEvent event, Pageable pageGui){
        gui.onPageRemoved(event, pageGui);
    }
    
    private class GuiIterator implements Iterator<GuiItem> {
        
        private int page = 0;
        private byte indexSlot = -1;
        
        @Override
        public boolean hasNext(){
            if(++indexSlot >= dynamicItems.length){
                indexSlot = 0;
                ++page;
            }
            return page < getCurrentPage() || indexSlot <= currentIndex;
        }
        
        @Override
        public GuiItem next(){
            return getPage(page).getItem(dynamicItems[indexSlot]);
        }
        
        @Override
        public void remove(){
            removeItem(page, dynamicItems[indexSlot]);
        }
    }
    
    @Override
    public IGui getBaseGui(){
        return gui;
    }
    
    @Override
    public @NotNull BaseGui setDeco(@NotNull Material type, int... slots){
        return gui.setDeco(type, slots);
    }
    
    @Override
    public void setDisplayName(int slot, @NotNull String name){
        gui.setDisplayName(slot, name);
    }
    
    @Override
    public void setDescription(int slot, @NotNull String... description){
        gui.setDescription(slot, description);
    }
    
    @Override
    public void setType(int slot, @NotNull Material material){
        gui.setType(slot, material);
    }
    
    @Override
    public void setGlowing(int slot, boolean glow){
        gui.setGlowing(slot, glow);
    }
    
    @Override
    @NotNull
    public IGui setItem(@NotNull GuiItem item){
        return gui.setItem(item);
    }
    
    @Override
    @NotNull
    public IGui setItem(int slot, @Nullable GuiItem item){
        return gui.setItem(slot, item);
    }
    
    @Override
    public void moveItem(int from, int to){
        gui.moveItem(from, to);
    }
    
    @Override
    public void moveItem(int from, @NotNull IGui guiTo, int to){
        gui.moveItem(from, guiTo, to);
    }
    
    @Override
    public @Nullable GuiItem getItem(int slot){
        return gui.getItem(slot);
    }
    
    @Override
    public void open(@NotNull ConsulatPlayer player){
        gui.open(player);
    }
    
    @Override
    public @NotNull String getName(){
        return gui.getName();
    }
    
    @Override
    public void setName(String name){
        gui.setName(name);
    }
    
    @Override
    public String buildInventoryTitle(){
        return gui.buildInventoryTitle();
    }
    
    @Override
    public void setTitle(){
        gui.setTitle();
    }
    
    @Override
    public void removeItem(int slot){
        gui.removeItem(slot);
    }
    
    @Override
    public @NotNull Inventory getInventory(){
        return gui.getInventory();
    }
    
    @Override
    public @NotNull List<GuiItem> getItems(){
        return gui.getItems();
    }
    
    @Override
    public void onCreate(){
        gui.onCreate();
    }
    
    @Override
    public void onOpen(GuiOpenEvent event){
        onPageOpen(event, this);
    }
    
    @Override
    public void onOpened(GuiOpenEvent event){
        onPageOpened(event, this);
    }
    @Override
    public void onClose(GuiCloseEvent event){
        onPageClose(event, this);
    }
    
    @Override
    public void onClick(GuiClickEvent event){
        onPageClick(event, this);
    }
    
    @Override
    public void onRemove(GuiRemoveEvent event){
        gui.onRemove(event);
    }
    
    @Override
    public boolean isModifiable(){
        return gui.isModifiable();
    }
    
    @Override
    public void setModifiable(boolean modifiable){
        gui.setModifiable(modifiable);
    }
    
    @Override
    public boolean isDestroyOnClose(){
        return gui.isDestroyOnClose();
    }
    
    @Override
    public void setDestroyOnClose(boolean destroyOnClose){
        gui.setDestroyOnClose(destroyOnClose);
    }
    
    @Override
    public boolean isBackButton(){
        return gui.isBackButton();
    }
    
    @Override
    public void setBackButton(boolean backButton){
        gui.setBackButton(backButton);
    }
    
    @Override
    public boolean containsFakeItems(){
        return gui.containsFakeItems();
    }
    
    @Override
    public IGui setFakeItem(int slot, ItemStack item, ConsulatPlayer player){
        return gui.setFakeItem(slot, item, player);
    }
}
