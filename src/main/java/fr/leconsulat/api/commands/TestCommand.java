package fr.leconsulat.api.commands;

import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

import java.util.Collections;

public class TestCommand extends ConsulatCommand {
    
    public TestCommand(){
        super("test", Collections.singletonList("test test"), "gugyu", 0, Rank.DEVELOPPEUR/*,
                (LiteralArgumentBuilder)LiteralArgumentBuilder.literal("test").then(
                        RequiredArgumentBuilder.argument("player", new ArgumentProfile()).suggests((context, builder)->{
                            return ICompletionProvider.a(((CommandListenerWrapper)context.getSource()).getServer().getPlayerList().m(), builder);
                        })
                )*/);
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        GuiManager.getInstance().getRootGui("yes").open(player, player);
    }
}
