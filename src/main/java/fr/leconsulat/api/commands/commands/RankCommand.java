package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.jetbrains.annotations.NotNull;

public class RankCommand extends ConsulatCommand {
    
    private final String bypassRank;
    
    public RankCommand(){
        super(ConsulatAPI.getConsulatAPI(), "rank");
        bypassRank = getPermission() + ".bypass-rank";
        RequiredArgumentBuilder<Object, ?> playerRequired = Arguments.playerList("joueur");
        for(Rank rank : Rank.values()){
            playerRequired.then(LiteralArgumentBuilder.literal(rank.getRankName()));
        }
        setDescription("Changer le grade d'un joueur").setUsage("/rank <joueur> <grade>").
                setArgsMin(2).setRank(Rank.RESPONSABLE).
                suggest(playerRequired);
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(args[0]);
        if(target == null){
            sender.sendMessage("§cJoueur ciblé introuvable ! §7(" + args[0] + ")");
            return;
        }
        String newRankName = args[1];
        Rank newRank = Rank.byName(newRankName);
        if(newRank == null){
            sender.sendMessage("§cUne erreur s'est produite. Le nouveau rang est peut-être invalide : " + newRankName);
            for(Rank rank : Rank.values()){
                sender.sendMessage(rank.getRankColor() + rank.getRankName() + ": " + rank.getRankPower());
            }
            return;
        }
        if(!sender.hasPower(newRank) && !sender.hasPermission(bypassRank)){
            sender.sendMessage("§cTu ne peux pas ajouter ce grade.");
            return;
        }
        target.setRank(newRank);
        sender.sendMessage("§a" + target.getName() + "§7 est désormais " + newRank.getRankColor() + newRank.getRankName());
    }
}
