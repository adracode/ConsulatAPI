package fr.leconsulat.api.gui.event;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Event appelé lorsque qu'un Gui est fermé
 */
public class GuiCloseEvent implements Cancellable {
    
    @NotNull private final ConsulatPlayer player;
    private boolean cancelled;
    private boolean openFatherGui = true;
    
    public GuiCloseEvent(@NotNull ConsulatPlayer player){
        this.player = player;
    }
    
    public @NotNull ConsulatPlayer getPlayer(){
        return player;
    }
    
    @Override
    public boolean isCancelled(){
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
    
    public boolean openFatherGui(){
        return openFatherGui;
    }
    
    public void setOpenFatherGui(boolean openFatherGui){
        this.openFatherGui = openFatherGui;
    }
}
