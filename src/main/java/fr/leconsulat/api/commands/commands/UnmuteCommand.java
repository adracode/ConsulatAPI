package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.moderation.ModerationDatabase;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class UnmuteCommand extends ConsulatCommand {
    
    public UnmuteCommand(){
        super(ConsulatAPI.getConsulatAPI(), "unmute");
        setDescription("Démuter un joueur").
                setUsage("/unmute <joueur> - Démute un joueur").
                setArgsMin(1).
                setRank(Rank.RESPONSABLE).
                suggest(Arguments.playerList("joueur"), Arguments.word("joueur"));
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            ModerationDatabase moderation = ConsulatAPI.getConsulatAPI().getModerationDatabase();
            moderation.unmute(args[0]);
            moderation.unmutePlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
        });
        sender.sendMessage(Text.MAYBE_UNMUTE_PLAYER);
    }
}
