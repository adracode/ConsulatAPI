package fr.leconsulat.api.gui.event;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lors d'un click sur un item dans un Gui
 */
public class GuiClickEvent {
    
    private final int slot;
    private final ClickType clickType;
    private final ConsulatPlayer player;
    
    public GuiClickEvent(int slot, @NotNull ClickType clickType, @NotNull ConsulatPlayer player){
        this.slot = slot;
        this.clickType = clickType;
        this.player = player;
    }
    
    public int getSlot(){
        return slot;
    }
    
    public @NotNull ClickType getClickType(){
        return clickType;
    }
    
    public @NotNull ConsulatPlayer getPlayer(){
        return player;
    }
    
    @Override
    public String toString(){
        return "GuiClickEvent{" +
                ", slot=" + slot +
                ", clickType=" + clickType +
                ", player=" + player +
                '}';
    }
}
