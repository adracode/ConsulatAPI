package fr.leconsulat.api.gui.gui.module.api;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.gui.IGui;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public interface MainPage extends Pageable {
    
    Pageable getPage(int page);
    
    void setDynamicItemsRange(int from, int to);
    
    void setDynamicItems(int... slots);
    
    void setTemplateItems(int... slots);
    
    ByteIterator getDynamicItems();
    
    void addPage(Pageable gui);
    
    void removePage(int page);
    
    int numberOfPages();
    
    int getCurrentPage();
    
    int getItemsNumber();
    
    byte getCurrentSlot();
    
    void addItem(GuiItem item);
    
    void removeItem(int page, int slot);
    
    void refreshItems();
    
    Pageable createPage();
    
    void removeAll();
    
    List<Pageable> getPages();
    
    @NotNull Iterator<GuiItem> iterator();
    
    @NotNull Iterator<GuiItem> reverseIterator();
    
    void setDisplayNamePages(int slot, @NotNull String name);
    
    void setDescriptionPages(int slot, @NotNull String... description);
    
    void setTypePages(int slot, @NotNull Material material);
    
    void setGlowingPages(int slot, boolean glow);
    
    @NotNull IGui setItemAll(@NotNull GuiItem item);
    
    @NotNull IGui setItemAll(int slot, @Nullable GuiItem item);
    
    void setSort(Comparator<GuiItem> comparator);
    
    void setTitle();
    
}
