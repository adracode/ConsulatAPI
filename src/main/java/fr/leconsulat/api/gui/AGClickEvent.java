package fr.leconsulat.api.gui;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.inventory.ClickType;

/**
 * Event appelé lors d'un click sur un item dans un Gui
 */
public class AGClickEvent {

    private final AGui gui;
    private final byte slot;
    private final ClickType clickType;
    private final ConsulatPlayer player;

    public AGClickEvent(AGui gui, byte slot, ClickType clickType, ConsulatPlayer player){
        this.gui = gui;
        this.slot = slot;
        this.clickType = clickType;
        this.player = player;
    }
    
    public AGui getGui(){
        return gui;
    }
    
    public byte getSlot(){
        return slot;
    }
    
    public ClickType getClickType(){
        return clickType;
    }
    
    public ConsulatPlayer getPlayer(){
        return player;
    }
}
