package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.PagedGui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lors d'un click sur un item dans un Gui
 */
public class GuiClickEvent<T> {
    
    private final PagedGui<T> pagedGui;
    private final Gui<T> gui;
    private final int slot;
    private final ClickType clickType;
    private final ConsulatPlayer player;
    
    public GuiClickEvent(PagedGui<T> pagedGui, @NotNull Gui<T> gui, int slot, @NotNull ClickType clickType, @NotNull ConsulatPlayer player){
        this.pagedGui = pagedGui;
        this.gui = gui;
        this.slot = slot;
        this.clickType = clickType;
        this.player = player;
    }
    
    public GuiItem getClickedItem(){
        return gui.getItem(slot);
    }
    
    @NotNull
    public Gui<T> getGui(){
        return gui;
    }
    
    public int getSlot(){
        return slot;
    }
    
    public int getPage(){
        return gui.getPage();
    }
    
    public void setPage(int page){
        gui.setPage(page);
    }
    
    @NotNull
    public ClickType getClickType(){
        return clickType;
    }
    
    @NotNull
    public T getKey(){
        return gui.getKey();
    }
    
    @NotNull
    public ConsulatPlayer getPlayer(){
        return player;
    }
    
    @Override
    public String toString(){
        return "GuiClickEvent{" +
                "gui=" + gui +
                ", slot=" + slot +
                ", clickType=" + clickType +
                ", player=" + player +
                '}';
    }
    
    public PagedGui<T> getPagedGui(){
        return pagedGui;
    }
}
