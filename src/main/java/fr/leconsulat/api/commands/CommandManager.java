package fr.leconsulat.api.commands;

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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommandManager implements Listener {
    
    private static CommandManager instance;
    
    private Object vanillaDispatcher;
    private Object nmsDispatcher;
    //private CommandDispatcher<CommandListenerWrapper> dispatcher;
    
    private Map<String, Command> bukkitCommands;
    private Method removeCommand;
    
    @SuppressWarnings("unchecked")
    public CommandManager(ConsulatAPI core){
        if(instance != null){
            return;
        }
        instance = this;
        /*vanillaDispatcher = ReflectionUtils.getDeclaredField(core.getDedicatedServer().getClass().getSuperclass(), "vanillaCommandDispatcher", core.getDedicatedServer());
        nmsDispatcher = ReflectionUtils.getDeclaredField(core.getDedicatedServer().getClass().getSuperclass(), "commandDispatcher", core.getDedicatedServer());
        dispatcher = (CommandDispatcher<CommandListenerWrapper>)ReflectionUtils.getDeclaredField(nmsDispatcher, "b");
        */
        core.getServer().getPluginManager().registerEvents(this, core);
        SimpleCommandMap commandMap = (SimpleCommandMap)ReflectionUtils.getDeclaredField(Bukkit.getServer(), "commandMap");
        if(commandMap == null){
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Couldn't find Map Command");
            bukkitCommands = new HashMap<>();
            return;
        }
        bukkitCommands = (Map<String, Command>)ReflectionUtils.getDeclaredField(SimpleCommandMap.class, "knownCommands", commandMap);
        if(bukkitCommands == null){
            bukkitCommands = new HashMap<>();
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Couldn't find Bukkit Commands");
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
        /*try {
            getHandle = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle");
            a = MinecraftReflection.getMinecraftClass("CommandDispatcher").getDeclaredMethod("a",
                    CommandNode.class, CommandNode.class, CommandListenerWrapper.class, Map.class);
            a.setAccessible(true);
            Field field = CraftServer.class.getDeclaredField("console");
            field.setAccessible(true);
            getCommandListener = MinecraftReflection.getEntityClass().getDeclaredMethod("getCommandListener");
        } catch(NoSuchMethodException | NoSuchFieldException e){
            e.printStackTrace();
        }*/
    }
    
    @EventHandler
    public void onPostInit(PostInitEvent e){
        bukkitCommands.remove("?");
        bukkitCommands.remove("bukkit:?");
        bukkitCommands.remove("about");
        bukkitCommands.remove("bukkit:about");
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
        bukkitCommands.remove("minecraft:msg");
        bukkitCommands.remove("teammsg");
        bukkitCommands.remove("minecraft:teammsg");
        bukkitCommands.remove("minecraft:tell");
        bukkitCommands.remove("tm");
        bukkitCommands.remove("minecraft:tm");
        bukkitCommands.remove("trigger");
        bukkitCommands.remove("minecraft:trigger");
        bukkitCommands.remove("w");
        bukkitCommands.remove("minecraft:w");
    }
    
    public void removeCommand(String command){
    
    }
    
    public void addCommand(Command command){
        bukkitCommands.put(command.getName(), command);
        for(String alias : command.getAliases()){
            bukkitCommands.put(alias, command);
        }
    }
    
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
    /*
    private Method getHandle;
    private Method a;
    private Method getCommandListener;
    
    public void sendCommands(ConsulatPlayer player){
        Object entityPlayer = null, entitygetCommandListener = null;
        try {
            entityPlayer = getHandle.invoke(player.getPlayer());
            entitygetCommandListener = getCommandListener.invoke(entityPlayer);
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map = Maps.newIdentityHashMap();
        RootCommandNode<ICompletionProvider> vanillaRoot = new RootCommandNode<>();
        RootCommandNode<CommandListenerWrapper> vanilla = dispatcher.getRoot();
        map.put(vanilla, vanillaRoot);
        try {
            a.invoke(nmsDispatcher, vanilla, vanillaRoot, entitygetCommandListener, map);
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        RootCommandNode<ICompletionProvider> rootcommandnode = new RootCommandNode<>();
        try {
            map.put(dispatcher.getRoot(), rootcommandnode);
            a.invoke(nmsDispatcher, dispatcher.getRoot(), rootcommandnode, getCommandListener.invoke(entityPlayer), map);
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        Collection<String> bukkit = new LinkedHashSet<>();
        
        for(CommandNode<ICompletionProvider> iCompletionProviderCommandNode : rootcommandnode.getChildren()){
            bukkit.add(iCompletionProviderCommandNode.getName());
        }
        
        PlayerCommandSendEvent event = new PlayerCommandSendEvent(player.getPlayer(), new LinkedHashSet<>(bukkit));
        event.getPlayer().getServer().getPluginManager().callEvent(event);
        System.out.println(bukkit);
        System.out.println(event.getCommands());
        for(String orig : bukkit){
            if(!event.getCommands().contains(orig)){
                rootcommandnode.removeCommand(orig);
            }
        }
         try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), PacketContainer.fromPacket(new PacketPlayOutCommands(rootcommandnode)));
        } catch(InvocationTargetException e){
            e.printStackTrace();
        }
    }*/
    
    public static CommandManager getInstance(){
        return instance;
    }
    /*
    public void suggest(LiteralArgumentBuilder<CommandListenerWrapper> suggestion){
        dispatcher.register(suggestion);
    }*/
}
