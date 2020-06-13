package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Cancellable;

/**
 * Event appelé lorsque qu'un Gui est fermé
 */
public class GuiCloseEvent<T> implements Cancellable {

    private final ConsulatPlayer player;
    private final Gui<T> gui;
    private final T key;
    private final Gui<?> father;
    private boolean openFatherGui;
    private boolean cancelled;
    
    public GuiCloseEvent(ConsulatPlayer player, Gui<T> gui, T key, Gui<?> father, boolean openFatherGui){
        this.player = player;
        this.gui = gui;
        this.key = key;
        this.father = father;
        this.openFatherGui = openFatherGui;
    }
    
    public Gui<T> getGui(){
        return gui;
    }
    
    public ConsulatPlayer getPlayer(){
        return player;
    }
    
    public boolean isOpenFatherGui(){
        return openFatherGui;
    }
    
    public void setOpenFatherGui(boolean openFatherGui){
        this.openFatherGui = openFatherGui;
    }
    
    public T getKey(){
        return key;
    }
    
    @Override
    public boolean isCancelled(){
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
    
    public Gui<?> getFather(){
        return father;
    }
}
