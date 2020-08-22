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
    private @Nullable Comparator<GuiItem> sort;
    
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
    }
    
    @Override
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
    }
    
    @Override
    public ByteIterator getDynamicItems(){
        return ByteIterators.wrap(dynamicItems);
    }
    
    @Override
    public MainPage getMainPage(){
        return this;
    }
    
    @Override
    public void addPage(Pageable gui){
        currentIndex = -1;
        pages.add(gui);
        gui.setPage(getCurrentPage());
    }
    
    @Override
    public IGui getGui(){
        return gui;
    }
    
    @Override
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
    }
    
    @Override
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
    }
    
    @Override
    public int numberOfPages(){
        return pages.size();
    }
    
    @Override
    public void onPageOpened(GuiOpenEvent event, Pageable pageGui){
        gui.onPageOpened(event, pageGui);
    }
    
    @Override
    public int getCurrentPage(){
        return numberOfPages() - 1;
    }
    
    @Override
    public void onPageClose(GuiCloseEvent event, Pageable pageGui){
        gui.onPageClose(event, pageGui);
        
    }
    
    @Override
    public final int getItemsNumber(){
        return currentIndex + 1 + getCurrentPage() * dynamicItems.length;
    }
    
    @Override
    public void onPageRemoved(GuiRemoveEvent event, Pageable pageGui){
        gui.onPageRemoved(event, pageGui);
    }
    
    @Override
    public byte getCurrentSlot(){
        return dynamicItems[currentIndex];
    }
    
    private void prepareAddItem(){
        int length = dynamicItems.length;
        if(++currentIndex >= length){
            newPage();
            //currentIndex == -1
            ++currentIndex;
        }
    }
    
    @Override
    public void addItem(GuiItem item){
        prepareAddItem();
        Pageable gui = getPage(getCurrentPage());
        if(sort != null){
            int page = 0, indexSlot = 0;
            for(ReversedGuiIterator iterator = this.reverseIterator(); iterator.hasPrevious(); ){
                GuiItem guiItem = iterator.previous();
                if(sort.compare(guiItem, item) < 0){
                    page = iterator.getPage();
                    indexSlot = iterator.getIndex();
                    if(indexSlot >= dynamicItems.length){
                        ++page;
                        indexSlot = 0;
                    }
                    break;
                }
                int currentSlot = guiItem.getSlot();
                boolean nextPage = currentSlot + 1 >= dynamicItems[dynamicItems.length - 1];
                if(nextPage){
                    getPage(iterator.getPage()).getGui().moveItem(currentSlot, getPage(iterator.getPage() + 1).getGui(), 0);
                } else {
                    getPage(iterator.getPage()).getGui().moveItem(currentSlot, currentSlot + 1);
                }
            }
            getPage(page).getGui().setItem(dynamicItems[indexSlot], item);
            return;
        }
        gui.getGui().setItem(getCurrentSlot(), item);
    }
    
    @Override
    public void removeItem(int page, int slot){
        int currentPage = getCurrentPage();
        byte currentSlot = getCurrentSlot();
        if(slot != currentSlot || currentPage != page){
            if(sort != null){
                for(GuiIterator iterator = this.iterator(page, Arrays.binarySearch(dynamicItems, (byte)slot) + 1); iterator.hasNext(); ){
                    int guiItemSlot = iterator.next().getSlot();
                    boolean previousPage = currentSlot - 1 < dynamicItems[0];
                    if(previousPage){
                        getPage(iterator.getPage()).getGui().moveItem(guiItemSlot, getPage(iterator.getPage() - 1).getGui(), 0);
                    } else {
                        getPage(iterator.getPage()).getGui().moveItem(guiItemSlot, guiItemSlot - 1);
                    }
                }
            } else {
                getPage(currentPage).getGui().moveItem(currentSlot, gui.getGui(), slot);
            }
        }
        getPage(currentPage).getGui().removeItem(currentSlot);
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
    public void refreshItems(){
        TreeSet<GuiItem> sortedItems = new TreeSet<>(sort);
        for(GuiIterator iterator = this.iterator(); iterator.hasNext(); ){
            sortedItems.add(iterator.next());
        }
        int index = -1, dynamicSize = dynamicItems.length;
        for(GuiItem item : sortedItems){
            setItem(++index / dynamicSize, dynamicItems[index % dynamicSize], item);
        }
    }
    
    private void setItem(int page, int slot, GuiItem item){
        getPage(page).getGui().setItem(slot, item);
    }
    
    @Override
    public void removeAll(){
        for(Iterator<GuiItem> iterator = this.iterator(); iterator.hasNext(); ){
            iterator.next();
            iterator.remove();
        }
    }
    
    @Override
    public List<Pageable> getPages(){
        return Collections.unmodifiableList(pages);
    }
    
    @Override
    public @NotNull GuiIterator iterator(){
        return new GuiIterator(0, 0);
    }
    
    @Override
    public @NotNull ReversedGuiIterator reverseIterator(){
        return new ReversedGuiIterator(getCurrentPage(), currentIndex);
    }
    
    public @NotNull GuiIterator iterator(int page, int index){
        return new GuiIterator(page, index);
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
    public void setSort(@NotNull Comparator<GuiItem> comparator){
        this.sort = comparator;
    }
    
    @Override
    public void setTitle(){
        for(int i = 1; i < pages.size(); i++){
            pages.get(i).getGui().setTitle();
        }
    }
    
    private class GuiIterator implements Iterator<GuiItem> {
        
        private ListIterator<GuiItem> items;
        private int currentIndex;
        
        public GuiIterator(int page, int index){
            List<GuiItem> items = new ArrayList<>(pages.size() * dynamicItems.length);
            for(Pageable pageable : pages){
                IGui gui = pageable.getGui();
                for(int slot : dynamicItems){
                    GuiItem item = gui.getItem(slot);
                    if(item == null){
                        break;
                    }
                    items.add(item);
                }
            }
            this.items = items.listIterator(Math.min(page * dynamicItems.length + index, items.size()));
            currentIndex = index - 1;
        }
        
        public int getPage(){
            return getIndex() / dynamicItems.length;
        }
        
        public int getIndex(){
            return currentIndex;
        }
        
        @Override
        public boolean hasNext(){
            return items.hasNext();
        }
        
        @Override
        public @NotNull GuiItem next(){
            GuiItem item = items.next();
            ++currentIndex;
            return item;
        }
        
        @Override
        public void remove(){
            removeItem(getPage(), dynamicItems[getIndex()]);
        }
        
    }
    
    private class ReversedGuiIterator implements ListIterator<GuiItem> {
        
        private ListIterator<GuiItem> items;
        private int currentIndex;
        
        public ReversedGuiIterator(int page, int index){
            List<GuiItem> items = new ArrayList<>(pages.size() * dynamicItems.length);
            for(Pageable pageable : pages){
                IGui gui = pageable.getGui();
                for(int slot : dynamicItems){
                    GuiItem item = gui.getItem(slot);
                    if(item == null){
                        break;
                    }
                    items.add(item);
                }
            }
            this.items = items.listIterator(Math.min(page * dynamicItems.length + index, items.size()));
            currentIndex = index + 1;
        }
        
        public int getPage(){
            return previousIndex() / dynamicItems.length;
        }
        
        public int getIndex(){
            return currentIndex % dynamicItems.length;
        }
        
        @Override
        public boolean hasNext(){
            throw new UnsupportedOperationException();
        }
        
        @Override
        public @NotNull GuiItem next(){
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean hasPrevious(){
            return items.hasPrevious();
        }
        
        @Override
        public @NotNull GuiItem previous(){
            GuiItem item = items.previous();
            --currentIndex;
            return item;
        }
        
        @Override
        public int nextIndex(){
            throw new UnsupportedOperationException();
        }
    
        @Override
        public int previousIndex(){
            return items.previousIndex();
        }
    
        @Override
        public void remove(){
            removeItem(getPage(), dynamicItems[getIndex()]);
        }
        
        @Override
        public void set(GuiItem item){
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(GuiItem item){
            throw new UnsupportedOperationException();
        }
    }
    
}
