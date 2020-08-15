package fr.leconsulat.api.gui.gui.module;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.*;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.MainPage;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.player.CPlayerManager;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MainPageGui<Gui extends IGui & MainPage> implements MainPage {
    
    private final Gui gui;
    
    private final List<Pageable> pages = new ArrayList<>();
    private byte currentIndex = -1;
    private byte[] dynamicItems;
    private byte[] templateItems;
    
    public MainPageGui(Gui gui){
        this.gui = gui;
        pages.add(this);
    }
    
    @NotNull
    public Pageable newPage(){
        Pageable pageGui = createPage();
        addPage(pageGui);
        onPageCreated(new GuiCreateEvent(gui), pageGui);
        return pageGui;
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
        if(to > gui.getLine() * 9 - 1){
            throw new IllegalArgumentException("A slot cannot be above the maximum slot ");
        }
        if(from > to){
            throw new IllegalArgumentException("To must be higher than from");
        }
        this.dynamicItems = new byte[to - from];
        for(byte i = (byte)from; i < to; i++){
            dynamicItems[i - from] = i;
        }
    }    @Override
    public final void setDynamicItems(int... slots){
        Set<Byte> sorted = new TreeSet<>();
        for(int slot : slots){
            sorted.add((byte)slot);
        }
        if(slots[0] < 0){
            throw new IllegalArgumentException("A slot cannot be below 0");
        }
        if(slots[slots.length - 1] > gui.getLine() * 9 - 1){
            throw new IllegalArgumentException("A slot cannot be above the maximum slot ");
        }
        this.dynamicItems = new byte[slots.length];
        int i = -1;
        for(byte slot : sorted){
            this.dynamicItems[++i] = slot;
        }
    }
    
    @Override
    public int getPage(){
        return 0;
    }    @Override
    public void setTemplateItems(int... slots){
        Set<Byte> sorted = new TreeSet<>();
        for(int slot : slots){
            sorted.add((byte)slot);
        }
        if(slots[0] < 0){
            throw new IllegalArgumentException("A slot cannot be below 0");
        }
        if(slots[slots.length - 1] > gui.getLine() * 9 - 1){
            throw new IllegalArgumentException("A slot cannot be above the maximum slot ");
        }
        this.templateItems = new byte[slots.length];
        int i = -1;
        for(byte slot : sorted){
            this.templateItems[++i] = slot;
        }
    }
    
    @Override
    public void setPage(int page){
    }    @Override
    public ByteIterator getDynamicItems(){
        return ByteIterators.wrap(dynamicItems);
    }
    
    @Override
    public MainPage getMainPage(){
        return this;
    }    @Override
    public void addPage(Pageable gui){
        currentIndex = -1;
        pages.add(gui);
        gui.setPage(getCurrentPage());
    }
    
    @Override
    public IGui getGui(){
        return gui;
    }    @Override
    public Pageable createPage(){
        if(templateItems == null || templateItems.length == 0){
            return new PageableGui(gui, gui.getName(), gui.getLine());
        }
        List<GuiItem> items = new ArrayList<>();
        for(byte templateSlot : templateItems){
            items.add(gui.getItem(templateSlot));
        }
        return new PageableGui(gui, gui.getName(), gui.getLine(), items.toArray(new GuiItem[0]));
    }
    
    @Override
    public void onPageCreated(GuiCreateEvent event, Pageable pageGui){
        gui.onPageCreated(event, pageGui);
    }
    
    @Override
    public void onPageClick(GuiClickEvent event, Pageable pageGui){
        gui.onPageClick(event, pageGui);
    }    @Override
    public void removePage(int page){
        Pageable removed = pages.remove(page);
        if(removed == null){
            return;
        }
        if(page != 0){
            Pageable previous = getPage(page - 1);
            for(HumanEntity human : new ArrayList<>(removed.getGui().getInventory().getViewers())){
                previous.getGui().open(CPlayerManager.getInstance().getConsulatPlayer(human.getUniqueId()));
            }
        }
        GuiRemoveEvent event = new GuiRemoveEvent();
        gui.onRemove(event);
        currentIndex = (byte)(dynamicItems.length - 1);
    }
    
    @Override
    public void onPageOpen(GuiOpenEvent event, Pageable pageGui){
        gui.onPageOpen(event, pageGui);
    }    @Override
    public int numberOfPages(){
        return pages.size();
    }
    
    @Override
    public void onPageOpened(GuiOpenEvent event, Pageable pageGui){
        gui.onPageOpened(event, pageGui);
    }    @Override
    public int getCurrentPage(){
        return numberOfPages() - 1;
    }
    
    @Override
    public void onPageClose(GuiCloseEvent event, Pageable pageGui){
        gui.onPageClose(event, pageGui);
        
    }    @Override
    public final int getItemsNumber(){
        return currentIndex + 1 + getCurrentPage() * dynamicItems.length;
    }
    
    @Override
    public void onPageRemoved(GuiRemoveEvent event, Pageable pageGui){
        gui.onPageRemoved(event, pageGui);
    }    @Override
    public byte getCurrentSlot(){
        return dynamicItems[currentIndex];
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
            return getPage(page).getGui().getItem(dynamicItems[indexSlot]);
        }
        
        @Override
        public void remove(){
            removeItem(page, dynamicItems[indexSlot]);
        }
    }    @Override
    public void addItem(GuiItem item){
        int length = dynamicItems.length;
        Pageable gui;
        if(++currentIndex >= length){
            gui = newPage();
            ++currentIndex;
        } else {
            gui = getPage(getCurrentPage());
        }
        gui.getGui().setItem(getCurrentSlot(), item);
    }
    
    @Override
    public void removeItem(int page, int slot){
        Pageable gui = getPage(page);
        gui.getGui().removeItem(slot);
        int currentPage = getCurrentPage();
        byte currentSlot = getCurrentSlot();
        if(slot != currentSlot || currentPage != page){
            Pageable lastGui = getPage(currentPage);
            lastGui.getGui().moveItem(currentSlot, gui.getGui(), slot);
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
    public void setDisplayNamePages(int slot, @NotNull String name){
        for(Pageable page : pages){
            page.getGui().setDisplayName(slot, name);
        }
    }
    
    @Override
    public void setDescriptionPages(int slot, @NotNull String... description){
        for(Pageable page : pages){
            page.getGui().setDescription(slot, description);
        }
        
    }
    
    @Override
    public void setTypePages(int slot, @NotNull Material material){
        for(Pageable page : pages){
            page.getGui().setType(slot, material);
        }
        
    }
    
    @Override
    public void setGlowingPages(int slot, boolean glow){
        for(Pageable page : pages){
            page.getGui().setGlowing(slot, glow);
        }
        
    }
    
    @Override
    public @NotNull IGui setItemAll(@NotNull GuiItem item){
        for(Pageable page : pages){
            page.getGui().setItem(item);
        }
        return gui;
    }
    
    @Override
    public @NotNull IGui setItemAll(int slot, @Nullable GuiItem item){
        for(Pageable page : pages){
            page.getGui().setItem(slot, item);
        }
        return gui;
    }
    
    @Override
    public void setTitle(){
        for(int i = 1; i < pages.size(); i++){
            pages.get(i).getGui().setTitle();
        }
    }
    

    

    

    

    

    

    

    

    

    

    

}
