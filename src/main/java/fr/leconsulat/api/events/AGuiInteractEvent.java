package fr.leconsulat.api.events;

import fr.leconsulat.api.gui.AGListener;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

public class AGuiInteractEvent extends Event {

    private final byte slot;
    private final ClickType click;
    private final AGListener listener;
    private final ConsulatPlayer player;

    public AGuiInteractEvent(AGListener listener, byte slot, ClickType click, ConsulatPlayer player){
        this.listener = listener;
        this.slot = slot;
        this.click = click;
        this.player = player;
    }

    public byte getSlot(){
        return slot;
    }

    public ClickType getClick(){
        return click;
    }

    public AGListener getGui(){
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
