package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.moderation.SanctionType;
import fr.leconsulat.api.moderation.sync.SanctionConfirm;
import fr.leconsulat.api.moderation.sync.SanctionPlayer;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.redis.RedisManager;
import fr.leconsulat.api.utils.StringUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RTopic;

public class KickCommand extends ConsulatCommand {
    
    private final RTopic kick = RedisManager.getInstance().getRedis().getTopic("Kick");
    private final Long2ObjectMap<SanctionConfirm> confirms = new Long2ObjectOpenHashMap<>();
    
    public KickCommand(){
        super(ConsulatAPI.getConsulatAPI(), "kick");
        setDescription("Expulser un joueur du serveur").
                setUsage("/kick <joueur> <raison> - Expulser un joueur").
                setArgsMin(2).
                setRank(Rank.MODO).
                suggest(Arguments.playerList("joueur")
                        .then(RequiredArgumentBuilder.argument("raison", StringArgumentType.greedyString())));
        RedisManager.getInstance().register("Kick", SanctionPlayer.class, (channel, player) -> {
            ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(player.getUUID());
            boolean applied = false;
            if(target != null){
                applied = true;
                Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () ->
                        target.getPlayer().kickPlayer(Text.KICK_PLAYER(player.getReason())));
            }
            RedisManager.getInstance().getRedis().getTopic("KickConfirm").publishAsync(
                    new SanctionConfirm(player.getId(), player.getUUID(), player.getSanctionerUUID(), SanctionType.KICK, applied));
        });
        RedisManager.getInstance().register("KickConfirm", SanctionConfirm.class, (channel, confirm) -> {
            if(confirms.containsKey(confirm.getId())){
                confirms.get(confirm.getId()).received(confirm.isApplied());
            }
        });
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            SanctionConfirm confirm = new SanctionConfirm(Bukkit.getOfflinePlayer(args[0]).getUniqueId(), sender.getUUID(), SanctionType.KICK, false);
            confirm.setOnComplete(applied -> {
                if(applied){
                    sender.sendMessage(Text.YOU_KICKED_PLAYER);
                } else {
                    sender.sendMessage(Text.PLAYER_NOT_CONNECTED);
                }
                confirms.remove(confirm.getId());
            });
            confirms.put(confirm.getId(), confirm);
            kick.publishAsync(
                    new SanctionPlayer(confirm.getId(), SanctionType.KICK, confirm.getSanctioned(), StringUtils.join(args, ' ', 1), sender.getUUID())).
                    onComplete((received, exception) -> confirm.setChannelsReceived((int)received.longValue()));
        });
    }
}
