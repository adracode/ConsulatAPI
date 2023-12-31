package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.guis.moderation.AntecedentsGui;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatOffline;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AntecedentsCommand extends ConsulatCommand {
    
    public AntecedentsCommand(){
        super(ConsulatAPI.getConsulatAPI(), "antecedents");
        setDescription("Voir les antécédents d'un joueur").
                setUsage("/antecedents <joueur> - Voir les antécédents d'un joueur").
                setArgsMin(1).
                setRank(Rank.RESPONSABLE).
                suggest(Arguments.playerList("joueur"));
        new AntecedentsGui.Container();
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        UUID uuid = CPlayerManager.getInstance().getPlayerUUID(args[0]);
        if(uuid == null){
            sender.sendMessage(Text.PLAYER_DOESNT_EXISTS);
            return;
        }
        GuiManager.getInstance().getContainer("antecedents").getGui(
                new ConsulatOffline(0, uuid, args[0], Rank.INVITE, null)).open(sender);
    }
}
