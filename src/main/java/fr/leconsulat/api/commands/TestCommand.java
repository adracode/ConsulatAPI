package fr.leconsulat.api.commands;

import fr.leconsulat.api.gui.AGuiManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

public class TestCommand extends ConsulatCommand {
    
    public TestCommand(){
        super("test", "test test", 0, Rank.DEVELOPPEUR);
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        AGuiManager.getInstance().getRootGui("yes").open(player, player);
    }
}
