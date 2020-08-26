package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.ConsulatServer;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.gui.exemples.ManageExemple;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.redis.RedisManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;

import java.util.UUID;

public class ADebugCommand extends ConsulatCommand {

    public static final @NotNull UUID UUID_PERMISSION = UUID.fromString("43da311c-d869-4e88-9b78-f1d4fc193ed4");
    private final @NotNull Object2IntMap<String> sub = new Object2IntOpenHashMap<>(1);

    public ADebugCommand(){
        super(ConsulatAPI.getConsulatAPI(), "adebug");
        setDescription("Commande de débug").setUsage("/adebug...").suggest(listener -> {
                    ConsulatPlayer player = getConsulatPlayer(listener);
                    return player != null && player.getUUID().equals(UUID_PERMISSION);
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
                        then(LiteralArgumentBuilder.literal("remove").
                                then(Arguments.playerList("joueur").
                                        then(Arguments.word("permission")))),
                LiteralArgumentBuilder.literal("guipage").
                        then(RequiredArgumentBuilder.argument("tick", IntegerArgumentType.integer(0)).
                                then(RequiredArgumentBuilder.argument("items", IntegerArgumentType.integer(0)))),
                LiteralArgumentBuilder.literal("config").
                        then(Arguments.word("server").suggests((context, builder) -> {
                            for(ConsulatServer server : ConsulatServer.values()){
                                builder.suggest(server.name());
                            }
                            return builder.buildFuture();
                        })),
                LiteralArgumentBuilder.literal("blockinventory").
                        then(Arguments.playerList("joueur").
                                then(RequiredArgumentBuilder.argument("valeur", BoolArgumentType.bool()))),
                LiteralArgumentBuilder.literal("item")
        );
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(UUID_PERMISSION);
        if(player != null){
            player.addPermission(getPermission());
        } else {
            ConsulatPlayer.addPermission(UUID_PERMISSION, getPermission());
        }
        new ManageExemple();
    }

    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        if(!sender.getUUID().equals(UUID_PERMISSION)){
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
                        return;
                    }
                    ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(args[2]);
                    if(target == null){
                        sender.sendMessage("§cJoueur non connecté");
                        return;
                    }
                    switch(args[1]){
                        case "has":
                            sender.sendMessage((target.hasPermission(args[3]) ? "§aIl a la permission " :
                                    "§cIl n'a pas la permission ") + args[3]);
                            break;
                        case "add":
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
                case "config":
                    ConsulatAPI api = ConsulatAPI.getConsulatAPI();
                    if(api.getConsulatServer() == ConsulatServer.UNKNOWN){
                        if(args.length > 1){
                            api.setServer(ConsulatServer.valueOf(args[1].toUpperCase()));
                            api.getConfig().set("server-name", args[1]);
                            api.saveConfig();
                            sender.sendMessage("§aNom serveur changé..");
                        }
                    }
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
            }
        }
    }
}
