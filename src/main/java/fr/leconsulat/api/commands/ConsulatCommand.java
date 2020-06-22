package fr.leconsulat.api.commands;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class ConsulatCommand extends Command implements Comparable<ConsulatCommand> {
    
    private static Method getEntity;
    
    static{
        try {
            getEntity = MinecraftReflection.getMinecraftClass("CommandListenerWrapper").getMethod("h");
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }
    
    private String usage;
    private int argsMin;
    private Rank rankNeeded;
    private String permission = "";
    
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
    
    @Nullable
    @Override
    public String getPermission(){
        return permission;
    }
    
    @Override
    public void setPermission(String permission){
        this.permission = permission;
    }
    
    @SafeVarargs
    protected final void suggest(boolean replace, ArgumentBuilder<Object, ?>... suggestions){
        CommandManager manager = CommandManager.getInstance();
        if(suggestions.length == 0){
            manager.suggest(LiteralArgumentBuilder.literal(this.getName()), replace);
            for(String aliases : this.getAliases()){
                manager.suggest(LiteralArgumentBuilder.literal(aliases), replace);
            }
            return;
        }
        for(ArgumentBuilder<Object, ?> suggestion : suggestions){
            manager.suggest(LiteralArgumentBuilder.literal(this.getName()).then(suggestion), replace);
        }
        for(String aliases : this.getAliases()){
            for(ArgumentBuilder<Object, ?> suggestion : suggestions){
                manager.suggest(LiteralArgumentBuilder.literal(aliases).then(suggestion), replace);
            }
        }
    }
    
    @SafeVarargs
    protected final void suggest(boolean replace, Predicate<Object> predicate, ArgumentBuilder<Object, ?>... suggestions){
        CommandManager manager = CommandManager.getInstance();
        if(suggestions.length == 0){
            manager.suggest(LiteralArgumentBuilder.literal(this.getName()).requires(predicate), replace);
            for(String aliases : this.getAliases()){
                manager.suggest(LiteralArgumentBuilder.literal(aliases).requires(predicate), replace);
            }
            return;
        }
        for(ArgumentBuilder<Object, ?> suggestion : suggestions){
            manager.suggest(LiteralArgumentBuilder.literal(this.getName()).requires(predicate).then(suggestion), replace);
        }
        for(String aliases : this.getAliases()){
            for(ArgumentBuilder<Object, ?> suggestion : suggestions){
                manager.suggest(LiteralArgumentBuilder.literal(aliases).requires(predicate).then(suggestion), replace);
            }
        }
    }
    
    protected static ConsulatPlayer getConsulatPlayer(Object commandListenerWrapper){
        try {
            Player player = (Player)MinecraftReflection.getBukkitEntity(getEntity.invoke(commandListenerWrapper));
            return player == null ? null : CPlayerManager.getInstance().getConsulatPlayer(player.getUniqueId());
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
        if(!player.hasPermission(getPermission()) && !player.hasPower(rankNeeded)){
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
