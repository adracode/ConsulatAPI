package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.gui.exemples.ManageExemple;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

import java.util.Collections;

public class TestCommand extends ConsulatCommand {
    
    public TestCommand(){
        super("test", Collections.singletonList("testtest"), "gugyu", 0, Rank.DEVELOPPEUR);
        new ManageExemple();
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        ManageExemple.getInstance().getGui(player).open(player);
        ManageExemple.getInstance().removeGui(player);
        GuiManager.getInstance().userInput(player, (input)->{}, new String[]{"Test"});
    }
}
