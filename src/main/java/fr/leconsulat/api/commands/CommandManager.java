package fr.leconsulat.api.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.events.PostInitEvent;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class CommandManager implements Listener {
    
    private static CommandManager instance;
    
    private CommandDispatcher<?> vanillaDispatcher;
    private CommandDispatcher<?> dispatcher;
    private Map<String, CommandNode<?>> vanillaChildren;
    private Map<String, CommandNode<?>> children;
    
    private Map<String, Command> commands;
    
    private Method updateCommands;
    
    @SuppressWarnings("unchecked")
    public CommandManager(ConsulatAPI core){
        if(instance != null){
            return;
        }
        instance = this;
        core.getServer().getPluginManager().registerEvents(this, core);
        SimpleCommandMap commandMap = (SimpleCommandMap)ReflectionUtils.getDeclaredField(Bukkit.getServer(), "commandMap");
        if(commandMap == null){
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Couldn't find Map Command");
            commands = new HashMap<>();
            return;
        }
        commands = (Map<String, Command>)ReflectionUtils.getDeclaredField(SimpleCommandMap.class, "knownCommands", commandMap);
        if(commands == null){
            commands = new HashMap<>();
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Couldn't find Bukkit Commands");
        }
        try {
            updateCommands = MinecraftReflection.getCraftPlayerClass().getMethod("updateCommands");
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        }
        ConsulatAPI.getConsulatAPI().getProtocolManager().addPacketListener(new PacketAdapter(ConsulatAPI.getConsulatAPI(),
                ListenerPriority.LOWEST, PacketType.Play.Server.COMMANDS) {
            @Override
            public void onPacketSending(PacketEvent event){
                if(CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId()) == null){
                    event.setCancelled(true);
                }
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.LOW)
    public void onPostInit(PostInitEvent e){
        ConsulatAPI core = ConsulatAPI.getConsulatAPI();
        vanillaDispatcher = (CommandDispatcher<?>)ReflectionUtils.getDeclaredField(ReflectionUtils.getDeclaredField(core.getDedicatedServer().getClass().getSuperclass(), "vanillaCommandDispatcher", core.getDedicatedServer()), "b");
        dispatcher = (CommandDispatcher<?>)ReflectionUtils.getDeclaredField(ReflectionUtils.getDeclaredField(core.getDedicatedServer().getClass().getSuperclass(), "commandDispatcher", core.getDedicatedServer()), "b");
        vanillaChildren = (Map<String, CommandNode<?>>)ReflectionUtils.getDeclaredField(CommandNode.class, "children", vanillaDispatcher.getRoot());
        children = (Map<String, CommandNode<?>>)ReflectionUtils.getDeclaredField(CommandNode.class, "children", dispatcher.getRoot());
        removeBukkitCommand("?");
        removeBukkitCommand("bukkit:?");
        removeBukkitCommand("about");
        removeBukkitCommand("bukkit:about");
        removeBukkitCommand("bukkit:help");
        removeBukkitCommand("pl");
        removeBukkitCommand("bukkit:pl");
        removeBukkitCommand("plugins");
        removeBukkitCommand("bukkit:plugins");
        removeBukkitCommand("ver");
        removeBukkitCommand("bukkit:ver");
        removeBukkitCommand("version");
        removeBukkitCommand("bukkit:version");
        removeMinecraftCommand("minecraft:help");
        removeMinecraftCommand("list");
        removeMinecraftCommand("minecraft:list");
        removeMinecraftCommand("me");
        removeMinecraftCommand("minecraft:me");
        removeMinecraftCommand("minecraft:msg");
        removeMinecraftCommand("teammsg");
        removeMinecraftCommand("minecraft:teammsg");
        removeMinecraftCommand("minecraft:tell");
        removeMinecraftCommand("tm");
        removeMinecraftCommand("minecraft:tm");
        removeMinecraftCommand("trigger");
        removeMinecraftCommand("minecraft:trigger");
        removeMinecraftCommand("w");
        removeMinecraftCommand("minecraft:w");
        children.remove("boutique");
        children.remove("consulatcore:boutique");
    }
    
    public void sendCommands(ConsulatPlayer player){
        try {
            updateCommands.invoke(player.getPlayer());
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
    
    public void removeBukkitCommand(String command){
        commands.remove(command);
        children.remove(command);
    }
    
    public void removeMinecraftCommand(String command){
        removeBukkitCommand(command);
        vanillaChildren.remove(command);
    }
    
    public void addCommand(Command command){
        commands.put(command.getName(), command);
        for(String alias : command.getAliases()){
            commands.put(alias, command);
        }
    }
    
    @EventHandler
    public void onCommandUpdate(PlayerCommandSendEvent event){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId());
        if(player == null){
            return;
        }
        for(Iterator<String> iterator = event.getCommands().iterator(); iterator.hasNext(); ){
            Command command = commands.get(iterator.next());
            if(!(command instanceof ConsulatCommand)){
                continue;
            }
            if(!player.hasPower(((ConsulatCommand)command).getRankNeeded())){
                iterator.remove();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(e.getPlayer().getUniqueId());
        int separator = e.getMessage().indexOf(' ');
        Command command = commands.get((separator == -1 ? e.getMessage().substring(1) : e.getMessage().substring(1, separator)).toLowerCase());
        if(command == null){
            e.setMessage("/help");
            return;
        }
        if(!(command instanceof ConsulatCommand)){
            return;
        }
        if(!player.hasPower(((ConsulatCommand)command).getRankNeeded())){
            e.setMessage("/help");
        }
    }

    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent e){
        Command command = bukkitCommands.get(e.getCommand().replaceFirst("/", "").split(" ")[0].toLowerCase());
        if(!(command instanceof ConsoleUsable)){
            e.setCommand("help");
        }
    }*/
    
    public void execute(CommandSender sender, String alias, String[] args){
        Command cmd = commands.get(alias);
        if(cmd instanceof ConsulatCommand){
            ConsulatCommand command = (ConsulatCommand)cmd;
            ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(((Player)sender).getUniqueId());
            command.onCommand(player, args);
        }
    }
    
    public Command getCommand(String command){
        return commands.get(command);
    }
    
    public Map<String, Command> getCommands(){
        return commands;
    }
    
    public static CommandManager getInstance(){
        return instance;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void suggest(LiteralArgumentBuilder<?> suggestion){
        dispatcher.register((LiteralArgumentBuilder)suggestion);
    }
}
