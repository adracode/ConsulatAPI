package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.GuiContainer;
import fr.leconsulat.api.gui.gui.template.DataRelatGui;
import fr.leconsulat.api.player.ConsulatPlayer;

public class ManageExemple extends GuiContainer<ConsulatPlayer> {
    
    private static ManageExemple instance;
    
    public ManageExemple(){
        if(instance != null){
            throw new IllegalStateException();
        }
        instance = this;
    }
    
    @Override
    public DataRelatGui<ConsulatPlayer> createGui(ConsulatPlayer player){
        return new TestGui(player);
    }
    
    public static ManageExemple getInstance(){
        return instance;
    }
}
