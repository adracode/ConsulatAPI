package fr.leconsulat.api.gui.gui.module.api;

import fr.leconsulat.api.gui.GuiItem;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public interface MainPage extends Pageable, Iterable<GuiItem> {
    
    Pageable getPage(int page);
    
    void setDynamicItemsRange(int from, int to);
    
    void setDynamicItems(int... slots);
    
    void addPage(Pageable gui);
    
    void removePage(int page);
    
    int numberOfPages();
    
    int getCurrentPage();
    
    int getItemsNumber();
    
    byte getCurrentSlot();
    
    void addItem(GuiItem item);
    
    void removeItem(int page, int slot);
    
    Pageable createPage();
    
    void removeAll();
    
    List<Pageable> getPages();
    
    @NotNull Iterator<GuiItem> iterator();
    
}
