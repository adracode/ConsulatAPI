package fr.leconsulat.api.gui;

import fr.leconsulat.api.player.ConsulatPlayer;

/**
 * Event appelé lorsqu'un Gui est ouvert
 */
public class AGOpenEvent {

    private final ConsulatPlayer player;
    private boolean cancelled;

    public AGOpenEvent(ConsulatPlayer player){
        this.player = player;
        this.cancelled = false;
    }

    public ConsulatPlayer getPlayer(){
        return player;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
}
