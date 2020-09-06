package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.gui.exemples.ManageExemple;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.redis.RedisManager;
import fr.leconsulat.api.utils.FileUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBucket;

import java.io.File;
import java.util.*;

public class ADebugCommand extends ConsulatCommand {
    
    public static final @NotNull Set<UUID> UUID_PERMISSION = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            UUID.fromString("43da311c-d869-4e88-9b78-f1d4fc193ed4"),
            UUID.fromString("3244dbb2-8872-4e24-bbb0-890a34c9a6dd")
    )));
    private final @NotNull Object2IntMap<String> sub = new Object2IntOpenHashMap<>(1);
    
    public ADebugCommand(){
        super(ConsulatAPI.getConsulatAPI(), "adebug");
        setDescription("Commande de débug").setUsage("/adebug...").suggest(listener -> {
                    ConsulatPlayer player = getConsulatPlayer(listener);
                    return player != null && UUID_PERMISSION.contains(player.getUUID());
                },
                LiteralArgumentBuilder.literal("redis").
                        then(LiteralArgumentBuilder.literal("sub").
                                then(Arguments.word("channel"))).
                        then(LiteralArgumentBuilder.literal("pub").
                                then(Arguments.word("channel").
                                        then(RequiredArgumentBuilder.argument("message", StringArgumentType.greedyString())))).
                        then(LiteralArgumentBuilder.literal("pubint").
                                then(Arguments.word("channel").
                                        then(RequiredArgumentBuilder.argument("message", IntegerArgumentType.integer())))).
                        then(LiteralArgumentBuilder.literal("get").
                                then(Arguments.word("key"))),
                LiteralArgumentBuilder.literal("permission").
                        then(LiteralArgumentBuilder.literal("has").
                                then(Arguments.playerList("joueur").
                                        then(Arguments.word("permission")))).
                        then(LiteralArgumentBuilder.literal("add").
                                then(Arguments.playerList("joueur").
                                        then(Arguments.word("permission")))).
                        then(LiteralArgumentBuilder.literal("addall").
                                then(Arguments.word("permission"))).
                        then(LiteralArgumentBuilder.literal("remove").
                                then(Arguments.playerList("joueur").
                                        then(Arguments.word("permission")))).
                        then(LiteralArgumentBuilder.literal("removeall").
                                then(Arguments.word("permission"))),
                LiteralArgumentBuilder.literal("guipage").
                        then(RequiredArgumentBuilder.argument("tick", IntegerArgumentType.integer(0)).
                                then(RequiredArgumentBuilder.argument("items", IntegerArgumentType.integer(0)))),
                LiteralArgumentBuilder.literal("blockinventory").
                        then(Arguments.playerList("joueur").
                                then(RequiredArgumentBuilder.argument("valeur", BoolArgumentType.bool()))),
                LiteralArgumentBuilder.literal("item"),
                LiteralArgumentBuilder.literal("crash"),
                LiteralArgumentBuilder.literal("debug").
                        then(RequiredArgumentBuilder.argument("valeur", BoolArgumentType.bool()))
        );
        new ManageExemple();
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        if(!UUID_PERMISSION.contains(sender.getUUID())){
            return;
        }
        if(args.length > 0){
            switch(args[0]){
                case "redis":
                    if(args.length > 2){
                        switch(args[1]){
                            case "sub":
                                if(sub.containsKey(args[2])){
                                    sender.sendMessage("§cDéjà sub.");
                                    return;
                                }
                                sub.put(args[2], RedisManager.getInstance().register(args[2], Object.class,
                                        (channel, object) -> {
                                            Player p = Bukkit.getPlayer(sender.getUUID());
                                            if(p != null){
                                                p.sendMessage("[" + channel + "] " + object);
                                            }
                                        }));
                                sender.sendMessage("§aSub.");
                                break;
                            case "unsub":
                                RedisManager.getInstance().getRedis().getTopic(args[2]).removeListener(sub.getInt(args[2]));
                                sender.sendMessage("§aUnsub.");
                                break;
                            case "pub":
                                RedisManager.getInstance().getRedis().getTopic(args[2]).publishAsync(StringUtils.join(args, ' ', 3, args.length));
                                sender.sendMessage("§aPub.");
                                break;
                            case "pubint":
                                RedisManager.getInstance().getRedis().getTopic(args[2]).publishAsync(Integer.parseInt(StringUtils.join(args, ' ', 3, args.length)));
                                sender.sendMessage("§aPub.");
                                break;
                            case "get":
                                RBucket<?> get = RedisManager.getInstance().getRedis().getBucket(args[2]);
                                get.getAsync().onComplete((object, throwable) -> {
                                    sender.sendMessage(object + "");
                                });
                                break;
                        }
                        break;
                    }
                    break;
                case "permission":{
                    if(args.length < 4){
                        if(args.length < 3){
                            return;
                        }
                        switch(args[1]){
                            case "addall":
                                Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                                    int count = 0;
                                    for(File file : FileUtils.getFiles(new File(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/"))){
                                        ConsulatPlayer.addPermission(UUID.fromString(file.getName().substring(0, 36)), args[2]);
                                        ++count;
                                    }
                                    sender.sendMessage("§aPermission donnée pour " + count + " joueurs");
                                });
                                for(ConsulatPlayer player : CPlayerManager.getInstance().getConsulatPlayers()){
                                    player.addPermission(args[2]);
                                }
                                break;
                            case "removeall":
                                Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                                    int count = 0;
                                    for(File file : FileUtils.getFiles(new File(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/"))){
                                        ConsulatPlayer.removePermission(UUID.fromString(file.getName().substring(0, 36)), args[2]);
                                        ++count;
                                    }
                                    sender.sendMessage("§aPermission retiré pour " + count + " joueurs");
                                });
                                for(ConsulatPlayer player : CPlayerManager.getInstance().getConsulatPlayers()){
                                    player.removePermission(args[2]);
                                }
                                break;
                        }
                    }
                    @Nullable ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(args[2]);
                    switch(args[1]){
                        case "has":
                            if(target == null){
                                Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                                    sender.sendMessage((ConsulatPlayer.hasPermission(Bukkit.getOfflinePlayer(args[2]).getUniqueId(), args[3]) ?
                                            "§aIl a la permission " :
                                            "§cIl n'a pas la permission ") + args[3]);
                                });
                            } else {
                                sender.sendMessage((target.hasPermission(args[3]) ? "§aIl a la permission " :
                                        "§cIl n'a pas la permission ") + args[3]);
                            }
                            break;
                        case "add":
                            if(target == null){
                                Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                                    ConsulatPlayer.addPermission(Bukkit.getOfflinePlayer(args[2]).getUniqueId(), args[3]);
                                    sender.sendMessage("§aPermission donnée s'il ne l'a pas");
                                });
                                return;
                            }
                            if(target.hasPermission(args[3])){
                                sender.sendMessage("§cIl a déjà la permission");
                                return;
                            }
                            if(args[3].contains(".command.") || args[3].contains("commands")){
                                target.addCommandPermission(args[3]);
                            } else {
                                target.addPermission(args[3]);
                            }
                            sender.sendMessage("§aPermission donnée");
                            break;
                        case "remove":
                            if(target == null){
                                Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                                    ConsulatPlayer.removePermission(Bukkit.getOfflinePlayer(args[2]).getUniqueId(), args[3]);
                                    sender.sendMessage("§aPermission retirée s'il ne l'a pas");
                                });
                                return;
                            }
                            if(!target.hasPermission(args[3])){
                                sender.sendMessage("§cIl n'a pas la permission");
                                return;
                            }
                            if(args[3].contains(".command.")){
                                target.removeCommandPermission(args[3]);
                            } else {
                                target.removePermission(args[3]);
                            }
                            sender.sendMessage("§aIl n'a plus la permission");
                            break;
                    }
                }
                break;
                case "guipage":
                    if(args.length < 3){
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () -> {
                        IGui open = sender.getCurrentlyOpen();
                        if(open instanceof Pageable){
                            for(int i = 0, size = Integer.parseInt(args[2]); i < size; ++i){
                                ((Pageable)open).getMainPage().addItem(IGui.getItem(open, "Test", (byte)-1, "adracode"));
                            }
                        } else {
                            sender.sendMessage("§cPas une page.");
                        }
                    }, Integer.parseInt(args[1]));
                    break;
                case "blockinventory":
                    if(args.length < 3){
                        return;
                    }
                    ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(args[1]);
                    if(target == null){
                        return;
                    }
                    target.setInventoryBlocked(Boolean.parseBoolean(args[2]));
                    break;
                case "item":
                    sender.sendMessage(ConsulatAPI.getNMS().getItem().getItemNameId(sender.getPlayer().getInventory().getItemInMainHand()));
                    break;
                case "debug":
                    if(args.length < 2){
                        return;
                    }
                    ConsulatAPI.getConsulatAPI().setDebug(Boolean.parseBoolean(args[1]));
                    break;
                case "crash":
                    if(!ConsulatAPI.getConsulatAPI().isDevelopment()){
                        return;
                    }
                    long start = System.currentTimeMillis();
                    while(true){
                        if(System.currentTimeMillis() - start >= 20_000L){
                            break;
                        }
                    }
                    break;
            }
        }
    }
}
