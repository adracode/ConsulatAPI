package fr.leconsulat.api.commands;

import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.ranks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ConsulatCommand extends Command implements Comparable<ConsulatCommand>  {
    
    private String usage;
    private int argsMin;
    private Rank rankNeeded;
    
    public ConsulatCommand(String name, String usage, int argsMin, Rank rankNeeded){
        this(name, new ArrayList<>(), usage, argsMin, rankNeeded);
    }
    
    public ConsulatCommand(String name, List<String> aliases, String usage, int argsMin, Rank rankNeeded){
        super(name, "", usage, aliases);
        this.usage = usage;
        this.argsMin = argsMin;
        this.rankNeeded = rankNeeded;
        CommandManager commandManager = CommandManager.getInstance();
        if(commandManager == null){
            throw new IllegalStateException("Command Manager is not instantiated");
        }
        commandManager.addCommand(this);
    }
    
    public abstract void onCommand(ConsulatPlayer player, String[] args);
    
    @Override
    public int compareTo(ConsulatCommand o){
        return this.getName().compareTo(o.getName());
    }
    
    //Native execution
    @Override
    public final boolean execute(CommandSender sender, String alias, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("§cIl faut être en jeu pour éxecuter cette commande.");
            return false;
        }
        Player bukkitPlayer = (Player)sender;
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(bukkitPlayer.getUniqueId());
        if(!player.hasPower(rankNeeded)){
            bukkitPlayer.sendMessage("§cTu n'as pas le power requis.");
            return false;
        }
        if(args.length < argsMin){
            sender.sendMessage(ChatColor.RED + usage);
            return false;
        }
        CommandManager.getInstance().execute(sender, alias, args);
        return true;
    }
    
    public Rank getRankNeeded(){
        return rankNeeded;
    }
    
    public String getUsage(){
        return usage;
    }
}
