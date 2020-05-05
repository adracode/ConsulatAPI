package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.player.ConsulatPlayer;

/**
 * Event appelé lorsque qu'un Gui est fermé
 */
public class GuiCloseEvent {

    private final ConsulatPlayer player;
    private Object key;
    private boolean openFatherGui;

    public GuiCloseEvent(ConsulatPlayer player, boolean openFatherGui){
        this.player = player;
        this.openFatherGui = openFatherGui;
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
    
    public Object getKey(){
        return key;
    }

    public void setKey(Object key){
        this.key = key;
    }
}
