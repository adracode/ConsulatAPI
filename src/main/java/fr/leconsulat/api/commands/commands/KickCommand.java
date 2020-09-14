package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public class KickCommand extends ConsulatCommand {
    
    public KickCommand(){
        super(ConsulatAPI.getConsulatAPI(), "kick");
        setDescription("Expulser un joueur du serveur").
                setUsage("/kick <joueur> <raison> - Expulser un joueur").
                setArgsMin(2).
                setRank(Rank.MODO).
                suggest(Arguments.playerList("joueur")
                        .then(RequiredArgumentBuilder.argument("raison", StringArgumentType.greedyString())));
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(args[0]);
        if(target == null){
            sender.sendMessage(Text.PLAYER_NOT_CONNECTED);
            return;
        }
        target.getPlayer().kickPlayer(Text.KICK_PLAYER(StringUtils.join(args, ' ', 1)));
        sender.sendMessage(Text.YOU_KICKED_PLAYER);
    }
}
