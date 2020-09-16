package fr.leconsulat.api.moderation;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.moderation.sync.SanctionPlayer;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.redis.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.redisson.api.RTopic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ModerationDatabase {
    
    private Connection connection;
    private RTopic ban = RedisManager.getInstance().getRedis().getTopic("Ban");
    private RTopic mute = RedisManager.getInstance().getRedis().getTopic("Mute");
    private RTopic unmute = RedisManager.getInstance().getRedis().getTopic("Unmute");
    
    public ModerationDatabase(){
        this.connection = ConsulatAPI.getDatabase();
        RedisManager.getInstance().register("Ban", SanctionPlayer.class, (channel, sanction) -> {
            ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(sanction.getUUID());
            if(target != null){
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(sanction.getId());
                Date date = calendar.getTime();
                Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                    target.getPlayer().kickPlayer(Text.KICK_PLAYER("§4" + sanction.getReason() + "\n§cJusqu'au: §4" + ConsulatAPI.getConsulatAPI().DATE_FORMAT.format(date)));
                });
            }
            Bukkit.broadcastMessage(Text.PLAYER_BANNED(Bukkit.getOfflinePlayer(sanction.getUUID()).getName()));
        });
        RedisManager.getInstance().register("Mute", SanctionPlayer.class, (channel, sanction) -> {
            ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(sanction.getUUID());
            if(target != null){
                MuteReason muteReason = MuteReason.valueOf(sanction.getReason());
                target.setMuted(true);
                target.setMuteExpireMillis(sanction.getId());
                target.setMuteReason(muteReason.getSanctionName());
                target.sendMessage("§cTu as été sanctionné. Tu ne peux plus parler pour: §4" + muteReason.getSanctionName());
                if(target.getMuteHistory().containsKey(muteReason)){
                    int number = target.getMuteHistory().get(muteReason);
                    target.getMuteHistory().put(muteReason, ++number);
                } else {
                    target.getMuteHistory().put(muteReason, 1);
                }
            }
            Bukkit.broadcastMessage(Text.PLAYER_MUTED(Bukkit.getOfflinePlayer(sanction.getUUID()).getName()));
        });
        RedisManager.getInstance().register("Unmute", String.class, (channel, uuid) -> {
            ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(UUID.fromString(uuid));
            if(target != null && target.isMuted()){
                target.setMuted(false);
            }
        });
    }
    
    public void addSanction(UUID uuid, String name, Player moderator, String sanctionType, String reason, long expireMillis, long applicationMillis) throws SQLException{
        PreparedStatement request = connection.prepareStatement("INSERT INTO antecedents(playeruuid, playername, modname, moduuid, sanction, reason, expire, applicated, cancelled, active) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        request.setString(1, uuid.toString());
        request.setString(2, name);
        request.setString(3, moderator.getName());
        request.setString(4, moderator.getUniqueId().toString());
        request.setString(5, sanctionType);
        request.setString(6, reason);
        request.setLong(7, expireMillis);
        request.setLong(8, applicationMillis);
        request.setBoolean(9, false);
        request.setBoolean(10, true);
        request.executeUpdate();
    }
    
    public void unmute(String playerName){
        try {
            PreparedStatement unmuteRequest = connection.prepareStatement("UPDATE antecedents SET active = '0', cancelled = '1' WHERE sanction = 'MUTE' AND playername = ? AND active = '1'");
            unmuteRequest.setString(1, playerName);
            unmuteRequest.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void unban(String playerName){
        try {
            PreparedStatement unbanRequest = connection.prepareStatement("UPDATE antecedents SET active = '0', cancelled = '1' WHERE sanction = 'BAN' AND playername = ? AND active = '1'");
            unbanRequest.setString(1, playerName);
            unbanRequest.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void banPlayer(SanctionPlayer sanction){
        ban.publishAsync(sanction);
    }
    
    public void mutePlayer(SanctionPlayer sanction){
        mute.publishAsync(sanction);
    }
    
    public void unmutePlayer(UUID uuid){
        unmute.publishAsync(uuid.toString());
    }
    
    public void setMute(Player player) throws SQLException{
        PreparedStatement request = connection.prepareStatement("SELECT * FROM antecedents WHERE playeruuid = ? AND sanction = 'MUTE' AND active = '1'");
        request.setString(1, player.getUniqueId().toString());
        ResultSet resultSet = request.executeQuery();
        if(resultSet.next()){
            long expireMute = resultSet.getLong("expire");
            if(System.currentTimeMillis() >= expireMute){
                PreparedStatement unmuteRequest = connection.prepareStatement("UPDATE antecedents SET active = '0' WHERE sanction = 'MUTE' AND playeruuid = ?");
                unmuteRequest.setString(1, player.getUniqueId().toString());
                unmuteRequest.executeUpdate();
                
            } else {
                ConsulatPlayer survivalPlayer = CPlayerManager.getInstance().getConsulatPlayer(player.getUniqueId());
                survivalPlayer.setMuted(true);
                survivalPlayer.setMuteReason(resultSet.getString("reason"));
                survivalPlayer.setMuteExpireMillis(expireMute);
            }
        }
    }
    
}
