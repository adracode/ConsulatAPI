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
import org.bukkit.craftbukkit.v1_14_R1.command.ServerCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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
    private String permission;
    
    public ConsulatCommand(String plugin, String name, String usage, int argsMin, Rank rankNeeded){
        this(plugin, name, Collections.emptyList(), usage, argsMin, rankNeeded);
    }
    
    public ConsulatCommand(String plugin, String name, String alias, String usage, int argsMin, Rank rankNeeded){
        this(plugin, name, Collections.singletonList(alias), usage, argsMin, rankNeeded);
    }
    
    public ConsulatCommand(String plugin, String name, List<String> aliases, String usage, int argsMin, Rank rankNeeded){
        super(name, "", usage, aliases);
        this.usage = usage;
        this.argsMin = argsMin;
        this.rankNeeded = rankNeeded;
        this.permission = plugin + ".command." + name;
        CommandManager commandManager = CommandManager.getInstance();
        if(commandManager == null){
            throw new IllegalStateException("Command Manager is not instantiated");
        }
        commandManager.addCommand(this);
    }
    
    @Override
    public @Nullable String getPermission(){
        return permission;
    }
    
    @Override
    public void setPermission(String permission){
        this.permission = permission;
    }
    
    @SafeVarargs
    protected final void suggest(ArgumentBuilder<Object, ?>... suggestions){
        suggest(true, suggestions);
    }
    
    @SafeVarargs
    protected final void suggest(Predicate<Object> predicate, ArgumentBuilder<Object, ?>... suggestions){
        suggest(true, predicate, suggestions);
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
    public final boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args){
        if(sender instanceof ServerCommandSender && this instanceof ConsoleUsable){
            if(args.length < argsMin){
                sender.sendMessage(ChatColor.RED + usage);
                return false;
            }
            ((ConsoleUsable)this).onConsoleUse(sender, args);
            return true;
        } else if(!(sender instanceof Player)){
            sender.sendMessage("§cCette commande ne peut pas être exécutée ici.");
            return false;
        }
        if(args.length < argsMin){
            sender.sendMessage(ChatColor.RED + usage);
            return false;
        }
        onCommand(CPlayerManager.getInstance().getConsulatPlayer(((Player)sender).getUniqueId()), args);
        return true;
    }
    
    public Rank getRankNeeded(){
        return rankNeeded;
    }
    
    public String getUsage(){
        return usage;
    }
}
