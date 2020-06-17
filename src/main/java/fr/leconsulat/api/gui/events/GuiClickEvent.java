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
    private final int slot;
    private final ClickType clickType;
    private final ConsulatPlayer player;
    
    public GuiClickEvent(@NotNull PagedGui<T> pagedGui, int slot, @NotNull ClickType clickType, @NotNull ConsulatPlayer player){
        this.pagedGui = pagedGui;
        this.slot = slot;
        this.clickType = clickType;
        this.player = player;
    }
    
    public GuiItem getClickedItem(){
        return pagedGui.getItem(slot);
    }
    
    @NotNull
    public PagedGui<T> getPagedGui(){
        return pagedGui;
    }
    
    public int getSlot(){
        return slot;
    }
    
    public int getPage(){
        return pagedGui.getPage();
    }
    
    @NotNull
    public ClickType getClickType(){
        return clickType;
    }
    
    @NotNull
    public T getData(){
        return getGui().getData();
    }
    
    @NotNull
    public ConsulatPlayer getPlayer(){
        return player;
    }
    
    public Gui<T> getGui(){
        return pagedGui.getGui();
    }
    
    @Override
    public String toString(){
        return "GuiClickEvent{" +
                "pagedGui=" + pagedGui +
                ", slot=" + slot +
                ", clickType=" + clickType +
                ", player=" + player +
                '}';
    }
}
