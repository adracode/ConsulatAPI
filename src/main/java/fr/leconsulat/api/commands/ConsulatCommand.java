package fr.leconsulat.api.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.craftbukkit.v1_14_R1.command.ServerCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class ConsulatCommand extends Command implements PluginIdentifiableCommand, Comparable<ConsulatCommand> {
    
    private final @NotNull Plugin plugin;
    private final @NotNull String permission;
    private int argsMin;
    private @Nullable Rank rankNeeded;
    
    public ConsulatCommand(@NotNull Plugin plugin, @NotNull String name){
        super(name);
        if(Objects.requireNonNull(name, "name").isEmpty()){
            throw new IllegalArgumentException("Command name can't be empty");
        }
        this.permission = (this.plugin = plugin).getName().toLowerCase() + ".command." + name;
    }
    
    public @Nullable Rank getRank(){
        return rankNeeded;
    }
    
    public ConsulatCommand setRank(@NotNull Rank rankNeeded){
        this.rankNeeded = rankNeeded;
        return this;
    }
    
    public @NotNull ConsulatCommand setAliases(@NotNull String... aliases){
        for(String alias : aliases){
            if(alias.isEmpty()){
                throw new IllegalArgumentException("An alias can't be empty");
            }
        }
        setAliases(Arrays.asList(aliases));
        return this;
    }
    
    public @NotNull ConsulatCommand setArgsMin(int argsMin){
        this.argsMin = argsMin;
        return this;
    }
    
    @SafeVarargs
    public final ConsulatCommand suggest(@NotNull ArgumentBuilder<Object, ?>... suggestions){
        suggest(true, suggestions);
        return this;
    }
    
    @SafeVarargs
    public final ConsulatCommand suggest(boolean replace, @NotNull ArgumentBuilder<Object, ?>... suggestions){
        CommandManager manager = CommandManager.getInstance();
        if(Objects.requireNonNull(suggestions).length == 0){
            manager.suggest(LiteralArgumentBuilder.literal(this.getName()), replace);
            for(String aliases : this.getAliases()){
                manager.suggest(LiteralArgumentBuilder.literal(aliases), replace);
            }
            return this;
        }
        LiteralArgumentBuilder<Object> command = LiteralArgumentBuilder.literal(getName());
        for(ArgumentBuilder<Object, ?> suggestion : suggestions){
            command.then(suggestion);
        }
        manager.suggest(command, replace);
        for(String alias : this.getAliases()){
            command = LiteralArgumentBuilder.literal(alias);
            for(ArgumentBuilder<Object, ?> suggestion : suggestions){
                command.then(suggestion);
            }
            manager.suggest(command, replace);
        }
        return this;
    }
    
    @SafeVarargs
    public final ConsulatCommand suggest(@NotNull Predicate<Object> predicate, @NotNull ArgumentBuilder<Object, ?>... suggestions){
        suggest(true, predicate, suggestions);
        return this;
    }
    
    @SafeVarargs
    public final ConsulatCommand suggest(boolean replace, @NotNull Predicate<Object> predicate, @NotNull ArgumentBuilder<Object, ?>... suggestions){
        CommandManager manager = CommandManager.getInstance();
        if(Objects.requireNonNull(suggestions).length == 0){
            manager.suggest(LiteralArgumentBuilder.literal(this.getName()).requires(predicate), replace);
            for(String aliases : this.getAliases()){
                manager.suggest(LiteralArgumentBuilder.literal(aliases).requires(predicate), replace);
            }
            return this;
        }
        LiteralArgumentBuilder<Object> command = LiteralArgumentBuilder.literal(getName()).requires(predicate);
        for(ArgumentBuilder<Object, ?> suggestion : suggestions){
            command.then(suggestion);
        }
        manager.suggest(command, replace);
        for(String alias : this.getAliases()){
            command = LiteralArgumentBuilder.literal(alias).requires(predicate);
            for(ArgumentBuilder<Object, ?> suggestion : suggestions){
                command.then(suggestion);
            }
            manager.suggest(command, replace);
        }
        return this;
    }
    
    public void register(){
        CommandManager.getInstance().addCommand(this);
    }
    
    public abstract void onCommand(@NotNull ConsulatPlayer player, @NotNull String[] args);
    
    //Native execution
    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args){
        if(sender instanceof ServerCommandSender && this instanceof ConsoleUsable){
            if(args.length < argsMin){
                sender.sendMessage("§c" + getUsage());
                return false;
            }
            ((ConsoleUsable)this).onConsoleUse(sender, args);
            return true;
        } else if(!(sender instanceof Player)){
            sender.sendMessage("§cCette commande ne peut pas être exécutée ici.");
            return false;
        }
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(((Player)sender).getUniqueId());
        if(args.length < argsMin){
            TextComponent usage = new TextComponent("§c§lErreur: §7Mauvaise syntaxe §o(survolez pour voir)§7.");
            usage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cUtilisation:\n§7" + getUsage()).create()));
            player.sendMessage(usage);
            return false;
        }
        onCommand(player, args);
        return true;
    }
    
    @Override
    public @NotNull String getPermission(){
        return permission;
    }
    
    @Override
    public @NotNull ConsulatCommand setDescription(@NotNull String description){
        this.description = description;
        super.setDescription(description);
        return this;
    }
    
    @Override
    public @NotNull ConsulatCommand setUsage(@NotNull String usage){
        super.setUsage(usage);
        return this;
    }
    
    @Override
    public @NotNull Plugin getPlugin(){
        return plugin;
    }
    
    @Override
    public int compareTo(ConsulatCommand o){
        return this.getName().compareTo(o.getName());
    }
    
    public static ConsulatPlayer getConsulatPlayerFromContext(Object source){
        Player player = ConsulatAPI.getNMS().getCommand().getPlayerFromContextSource(source);
        return player == null ? null : CPlayerManager.getInstance().getConsulatPlayer(player.getUniqueId());
    }
    
    protected static ConsulatPlayer getConsulatPlayer(Object commandListenerWrapper){
        Player player = ConsulatAPI.getNMS().getCommand().getPlayerFromListener(commandListenerWrapper);
        return player == null ? null : CPlayerManager.getInstance().getConsulatPlayer(player.getUniqueId());
    }
}
