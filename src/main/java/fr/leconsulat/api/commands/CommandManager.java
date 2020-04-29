package fr.leconsulat.api.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.leconsulat.api.ConsulatAPI;
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
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommandManager implements Listener {
    
    private static CommandManager instance;
    
    private Map<String, Command> bukkitCommands;
    private Method removeCommand;
    
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
            bukkitCommands = new HashMap<>();
            return;
        }
        try {
            Field field = ReflectionUtils.getDeclaredField(SimpleCommandMap.class, "knownCommands");
            if(field == null){
                ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Couldn't find class knownCommands");
            } else {
                field.setAccessible(true);
                bukkitCommands = (Map<String, Command>)field.get(commandMap);
            }
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
        if(bukkitCommands == null){
            bukkitCommands = new HashMap<>();
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Couldn't find Bukkit Commands");
        } else {
            bukkitCommands.remove("?");
            bukkitCommands.remove("bukkit:?");
            bukkitCommands.remove("about");
            bukkitCommands.remove("bukkit:about");
            bukkitCommands.remove("help");
            bukkitCommands.remove("bukkit:help");
            bukkitCommands.remove("pl");
            bukkitCommands.remove("bukkit:pl");
            bukkitCommands.remove("plugins");
            bukkitCommands.remove("bukkit:plugins");
            bukkitCommands.remove("ver");
            bukkitCommands.remove("bukkit:ver");
            bukkitCommands.remove("version");
            bukkitCommands.remove("bukkit:version");
            bukkitCommands.remove("minecraft:help");
            bukkitCommands.remove("list");
            bukkitCommands.remove("minecraft:list");
            bukkitCommands.remove("me");
            bukkitCommands.remove("minecraft:me");
            bukkitCommands.remove("msg");
            bukkitCommands.remove("minecraft:msg");
            bukkitCommands.remove("teammsg");
            bukkitCommands.remove("minecraft:teammsg");
            bukkitCommands.remove("tell");
            bukkitCommands.remove("minecraft:tell");
            bukkitCommands.remove("tm");
            bukkitCommands.remove("minecraft:tm");
            bukkitCommands.remove("trigger");
            bukkitCommands.remove("minecraft:trigger");
            bukkitCommands.remove("w");
            bukkitCommands.remove("minecraft:w");
        }
        
        /*ConsulatAPI.getConsulatAPI().getProtocolManager().addPacketListener(new PacketAdapter(ConsulatAPI.getConsulatAPI(),
                ListenerPriority.LOWEST, PacketType.Play.Server.COMMANDS) {
            @Override
            public void onPacketSending(PacketEvent event){
                if(CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId()) == null){
                    event.setCancelled(true);
                }
            }
        });
        try {
            removeCommand = Class.forName("com.mojang.brigadier.tree.CommandNode").getDeclaredMethod("removeCommand", String.class);
        } catch(NoSuchMethodException | ClassNotFoundException e){
            e.printStackTrace();
        }*/
    }
    
    public void addCommand(Command command){
        bukkitCommands.put(command.getName(), command);
        for(String alias : command.getAliases()){
            bukkitCommands.put(alias, command);
        }
    }
    
    /*public void sendCommands(ConsulatPlayer player){
        for(Command command : bukkitCommands.values()){
            if(command instanceof ConsulatCommand){
                ConsulatCommand consulatCommand = (ConsulatCommand)command;
                
            }
        }
    }*/
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e){
        //ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(e.getPlayer().getUniqueId());
        /*if(player.hasPermission("api.bypass.command")){
            return;
        }*/
        //Command command = bukkitCommands.get(e.getMessage().substring(1, e.getMessage().indexOf(' ')).toLowerCase());
        /*if(!(command instanceof ConsulatCommand)){
            e.setMessage("/help");
            return;
        }*/
        /*if(!player.hasPermission(command.getPermission())){
            e.setMessage("/help");
        }*/
    }

    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent e){
        Command command = bukkitCommands.get(e.getCommand().replaceFirst("/", "").split(" ")[0].toLowerCase());
        if(!(command instanceof ConsoleUsable)){
            e.setCommand("help");
        }
    }*/
    
    public void execute(CommandSender sender, String alias, String[] args){
        Command cmd = bukkitCommands.get(alias);
        if(cmd instanceof ConsulatCommand){
            ConsulatCommand command = (ConsulatCommand)cmd;
            ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(((Player)sender).getUniqueId());
            command.onCommand(player, args);
        }
    }
    
    public Command getCommand(String command){
        return bukkitCommands.get(command);
    }
    
    public Map<String, Command> getCommands(){
        return bukkitCommands;
    }
    
    public static CommandManager getInstance(){
        return instance;
    }
}
