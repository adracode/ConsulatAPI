package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.guis.moderation.SanctionGui;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatOffline;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SanctionCommand extends ConsulatCommand {
    
    public SanctionCommand(){
        super(ConsulatAPI.getConsulatAPI(), "sanction");
        setDescription("Sanctionner un joueur (mute/ban)").
                setUsage("/sanction <joueur> - Sanctionner un joueur").
                setArgsMin(1).
                setRank(Rank.MODO).
                suggest(Arguments.playerList("joueur"));
        new SanctionGui.Container();
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        UUID uuid = CPlayerManager.getInstance().getPlayerUUID(args[0]);
        if(uuid == null){
            sender.sendMessage(Text.PLAYER_DOESNT_EXISTS);
            return;
        }
        GuiManager.getInstance().getContainer("sanctions").getGui(
                new ConsulatOffline(0, uuid, args[0], Rank.INVITE, null)).open(sender);
    }
}