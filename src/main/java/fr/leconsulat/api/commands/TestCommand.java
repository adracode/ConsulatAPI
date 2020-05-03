package fr.leconsulat.api.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

import java.util.Collections;

public class TestCommand extends ConsulatCommand {
    
    @SuppressWarnings("ConstantConditions")
    public TestCommand(){
        super("test", Collections.singletonList("testtest"), "gugyu", 0, Rank.DEVELOPPEUR);
        suggest(LiteralArgumentBuilder.literal("test").then(
                        Arguments.operators("salut")));
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        GuiManager.getInstance().getRootGui("yes").open(player, player);
    }
}
