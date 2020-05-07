package fr.leconsulat.api.commands;

import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

import java.util.Collections;

public class TestCommand extends ConsulatCommand {
    
    public TestCommand(){
        super("test", Collections.singletonList("testtest"), "gugyu", 0, Rank.DEVELOPPEUR);
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        GuiManager.getInstance().getRootGui("yes").open(player, player);
    }
}
