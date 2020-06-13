package fr.leconsulat.api.gui;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PagedGui<T> implements Iterable<GuiItem> {
    
    private List<Gui<T>> guis = new ArrayList<>();
    private GuiListener<T> listener;
    
    private byte currentIndex = -1;
    
    public PagedGui(GuiListener<T> listener){
        this.listener = listener;
    }
    
    public int getCurrentPage(){
        return guis.size() - 1;
    }
    
    public int pages(){
        return guis.size();
    }
    
    public Gui<T> getGui(int page){
        return guis.get(page);
    }
    
    public void addGui(Gui<T> gui){
        gui.setPagedGui(this);
        currentIndex = -1;
        guis.add(gui);
        gui.setPage(getCurrentPage());
        if(getCurrentPage() != 0){
            Gui<T> firstGui = getGui(0);
            if(firstGui.getFather() != null){
                gui.setFather(getGui(0).getFather());
            }
        }
    }
    
    public void addItem(GuiItem item){
        int length = listener.getLengthMoveableSlot();
        Gui<T> gui;
        if(++currentIndex >= length){
            gui = listener.getGui(getKey(), guis.size());
            ++currentIndex;
        } else {
            gui = listener.getGui(getKey(), getCurrentPage());
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
        Gui gui = getGui(page);
        gui.removeItem(slot);
        int currentPage = getCurrentPage();
        byte currentSlot = getCurrentSlot();
        if(slot != currentSlot || currentPage != page){
            Gui lastGui = getGui(currentPage);
            lastGui.moveItem(currentSlot, gui, slot);
        }
        if(currentSlot <= 0){
            if(currentPage > 0){
                listener.removePage(getKey(), currentPage);
            } else {
                currentIndex = -1;
            }
        } else {
            --currentIndex;
        }
    }
    
    public boolean removeGui(int page){
        guis.remove(page);
        currentIndex = (byte)(listener.getLengthMoveableSlot() - 1);
        return true;
    }
    
    public T getKey(){
        return this.guis.get(0).getKey();
    }
    
    @NotNull
    @Override
    public Iterator<GuiItem> iterator(){
        return new GuiIterator();
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
            return getGui(page).getItem(getCurrentSlot(indexSlot));
        }
        
        @Override
        public void remove(){
            removeItem(page, getCurrentSlot(indexSlot));
        }
    }
    
}
