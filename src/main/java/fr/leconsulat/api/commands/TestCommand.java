package fr.leconsulat.api.commands;

import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.gui.exemples.TestGui;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

import java.util.Collections;

public class TestCommand extends ConsulatCommand {
    
    private TestGui testGui = new TestGui();
    
    public TestCommand(){
        super("test", Collections.singletonList("testtest"), "gugyu", 0, Rank.DEVELOPPEUR);
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        //testGui.getGui(player).open(player);
        GuiManager.getInstance().userInput(player.getPlayer(), (input)->{}, new String[]{"Test"});
    }
}
