package fr.leconsulat.api.commands;

import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.ranks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ConsulatCommand implements CommandExecutor {

    private String usage;
    private int argsMin;
    private Rank rankMinimum;

    public ConsulatCommand(String usage, int argsMin, Rank rankMinimum) {
        this.usage = usage;
        this.argsMin = argsMin;
        this.rankMinimum = rankMinimum;
    }

    public abstract void onCommand(ConsulatPlayer player, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cIl faut être en jeu pour éxecuter cette commande.");
            return false;
        }
        if(args.length < argsMin){
            sender.sendMessage(ChatColor.RED + usage);
            return false;
        }
        Player bukkitPlayer = (Player)sender;
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(bukkitPlayer.getUniqueId());
        if(!player.hasPower(rankMinimum)){
            bukkitPlayer.sendMessage("§cTu n'as pas le power requis.");
            return false;
        }
        onCommand(player, args);
        return true;
    }
    
    public String getUsage(){
        return usage;
    }
}
