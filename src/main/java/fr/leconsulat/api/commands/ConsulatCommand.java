package fr.leconsulat.api.commands;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.utils.ReflectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ConsulatCommand extends Command implements Comparable<ConsulatCommand> {
    
    private static Method getEntity;
    
    static {
        try {
            getEntity = MinecraftReflection.getMinecraftClass("CommandListenerWrapper").getMethod("h");
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }
    
    private String usage;
    private int argsMin;
    private Rank rankNeeded;
    
    public ConsulatCommand(String name, String usage, int argsMin, Rank rankNeeded){
        this(name, Collections.emptyList(), usage, argsMin, rankNeeded);
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
    
    protected void suggest(LiteralArgumentBuilder<?> suggestion){
        CommandManager.getInstance().suggest(suggestion);
    }
    
    /*public ConsulatCommand(String name, List<String> aliases, String usage, int argsMin, Rank rankNeeded, LiteralArgumentBuilder<CommandListenerWrapper> suggestion){
        this(name, aliases, usage, argsMin, rankNeeded);
        CommandManager.getInstance().suggest(suggestion);
    }*/
    
    protected static ConsulatPlayer getConsulatPlayer(Object commandListenerWrapper){
        try {
            Player player = (Player)MinecraftReflection.getBukkitEntity(getEntity.invoke(commandListenerWrapper));
            if(player == null){
                return null;
            }
            return CPlayerManager.getInstance().getConsulatPlayer(player.getUniqueId());
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
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
