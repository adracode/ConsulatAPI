package fr.leconsulat.api.gui.event;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lorsqu'un Gui est ouvert
 */
public class GuiOpenEvent implements Cancellable {
    
    private final @NotNull ConsulatPlayer player;
    private boolean cancelled;

    public GuiOpenEvent(@NotNull ConsulatPlayer player){
        this.player = player;
        this.cancelled = false;
    }
    
    public @NotNull ConsulatPlayer getPlayer(){
        return player;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
}
