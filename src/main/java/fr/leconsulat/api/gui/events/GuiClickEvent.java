package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.inventory.ClickType;

/**
 * Event appelé lors d'un click sur un item dans un Gui
 */
public class GuiClickEvent {
    
    private final Gui gui;
    private final int slot;
    private final ClickType clickType;
    private final ConsulatPlayer player;
    
    public GuiClickEvent(Gui gui, int slot, ClickType clickType, ConsulatPlayer player){
        this.gui = gui;
        this.slot = slot;
        this.clickType = clickType;
        this.player = player;
    }
    
    public Gui getGui(){
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
    
    public ClickType getClickType(){
        return clickType;
    }
    
    public Object getKey(){
        return gui.getKey();
    }
    
    public ConsulatPlayer getPlayer(){
        return player;
    }
}
