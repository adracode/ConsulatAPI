package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.GuiListener;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

public class GuiInteractEvent extends Event {

    private final int slot;
    private final ClickType click;
    private final GuiListener listener;
    private final ConsulatPlayer player;

    public GuiInteractEvent(GuiListener listener, int slot, ClickType click, ConsulatPlayer player){
        this.listener = listener;
        this.slot = slot;
        this.click = click;
        this.player = player;
    }

    public int getSlot(){
        return slot;
    }

    public ClickType getClick(){
        return click;
    }

    public GuiListener getGui(){
        return listener;
    }

    public ConsulatPlayer getPlayer(){
        return player;
    }

    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
