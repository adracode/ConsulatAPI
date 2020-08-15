package fr.leconsulat.api.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.RootCommandNode;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.events.PostInitEvent;
import fr.leconsulat.api.nms.api.server.DedicatedServer;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.*;
import java.util.logging.Level;

public class CommandManager implements Listener {

    private static CommandManager instance;

    static {
        new CommandManager();
    }
    
    private final Map<String, Command> commands;
    private CommandDispatcher<?> dispatcher;
    private RootCommandNode<?> vanillaNode;
    private RootCommandNode<?> node;
    private List<Runnable> postInit = new ArrayList<>();
    
    @SuppressWarnings("unchecked")
    private CommandManager(){
        if(instance != null){
            throw new IllegalStateException();
        }
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, ConsulatAPI.getConsulatAPI());
        SimpleCommandMap commandMap = ConsulatAPI.getNMS().getServer().getDedicatedServer().getCommandMap();
        if(commandMap == null){
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Couldn't find Map Command");
            commands = null;
            return;
        }
        commands = (Map<String, Command>)ReflectionUtils.getDeclaredField(SimpleCommandMap.class, "knownCommands", commandMap);
        if(commands == null){
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Couldn't find Bukkit Commands");
            Bukkit.shutdown();
            return;
        }
        ConsulatAPI.getConsulatAPI().getProtocolManager().addPacketListener(new PacketAdapter(ConsulatAPI.getConsulatAPI(), ListenerPriority.LOWEST,
                PacketType.Play.Server.COMMANDS) {
            @Override
            public void onPacketSending(PacketEvent event){
                try {
                    if(CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId()) == null){
                        event.setCancelled(true);
                    }
                } catch(UnsupportedOperationException e){
                    event.setCancelled(true);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostInit(PostInitEvent e){
        DedicatedServer server = ConsulatAPI.getNMS().getServer().getDedicatedServer();
        dispatcher = server.getCommandDispatcher();
        vanillaNode = server.getVanillaCommandDispatcher().getRoot();
        node = server.getCommandDispatcher().getRoot();
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
        node.removeCommand("boutique");
        node.removeCommand("consulatcore:boutique");
        for(Runnable postInit : this.postInit){
            postInit.run();
        }
        postInit = null;
    }

    @EventHandler
    public void onCommandUpdate(PlayerCommandSendEvent event){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId());
        if(player == null){
            event.getCommands().clear();
            return;
        }
        for(Iterator<String> iterator = event.getCommands().iterator(); iterator.hasNext(); ){
            Command command = commands.get(iterator.next());
            if(!(command instanceof ConsulatCommand)){
                if(player.hasPermission(ConsulatAPI.getConsulatAPI().getPermission("allow-other-plugin-commands"))){
                    continue;
                }
                if(command instanceof PluginIdentifiableCommand){
                    if(!player.hasPermission(((PluginIdentifiableCommand)command).getPlugin().getName().toLowerCase() + ".commands")){
                        iterator.remove();
                    }
                }
                continue;
            }
            if(!player.hasPermission(command.getPermission())){
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
            e.setMessage("/help commands");
            return;
        }
        if(!(command instanceof ConsulatCommand)){
            if(player.hasPermission(ConsulatAPI.getConsulatAPI().getPermission("allow-other-plugin-commands"))){
                return;
            }
            if(command instanceof PluginIdentifiableCommand){
                if(!player.hasPermission(((PluginIdentifiableCommand)command).getPlugin().getName().toLowerCase() + ".commands")){
                    e.setMessage("/help commands");
                    return;
                }
            }
        }
        if(!player.hasPermission(command.getPermission())){
            e.setMessage("/help commands");
        }
    }

    public Command getCommand(String command){
        return commands.get(command);
    }

    public void addCommand(Command command){
        commands.put(command.getName(), command);
        for(String alias : command.getAliases()){
            commands.put(alias, command);
        }
    }

    public void removeBukkitCommand(String commandName){
        Command command = commands.get(commandName);
        if(command == null){
            return;
        }
        for(String alias : command.getAliases()){
            commands.remove(alias);
            node.removeCommand(alias);
        }
        commands.remove(commandName);
        node.removeCommand(commandName);
    }

    public void removeMinecraftCommand(String commandName){
        Command command = commands.get(commandName);
        if(command == null){
            return;
        }
        for(String alias : command.getAliases()){
            vanillaNode.removeCommand(alias);
        }
        removeBukkitCommand(commandName);
        vanillaNode.removeCommand(commandName);
    }

    public void sendCommands(ConsulatPlayer player){
        player.getPlayer().updateCommands();
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void suggest(LiteralArgumentBuilder<?> suggestion, boolean replace){
        postInit.add(()-> {
            if(replace){
                String command = suggestion.getLiteral();
                if(node.getChild(command) != null){
                    node.removeCommand(command);
                }
                if(vanillaNode.getChild(command) != null){
                    vanillaNode.removeCommand(command);
                }
            }
            dispatcher.register((LiteralArgumentBuilder)suggestion);
        });
    }

    public Map<String, Command> getCommands(){
        return Collections.unmodifiableMap(commands);
    }

    public static CommandManager getInstance(){
        return instance;
    }
}
