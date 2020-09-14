package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class UnbanCommand extends ConsulatCommand {
    
    public UnbanCommand(){
        super(ConsulatAPI.getConsulatAPI(), "unban");
        setDescription("Débannir un joueur").
                setUsage("/unban <joueur> - Débannir un joueur").
                setArgsMin(1).
                setRank(Rank.RESPONSABLE).
                suggest(Arguments.word("joueur"));
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        String playerName = args[0];
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            ConsulatAPI.getConsulatAPI().getModerationDatabase().unban(playerName);
            sender.sendMessage(Text.MAYBE_UNBAN_PLAYER);
        });
    }
}
