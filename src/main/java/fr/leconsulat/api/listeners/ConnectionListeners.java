package fr.leconsulat.api.listeners;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.player.PlayersManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        try {
            ConsulatPlayer consulatPlayer = PlayersManager.fetchPlayer(player);
            if(consulatPlayer != null){
                PlayersManager.initializePlayer(player, PlayersManager.fetchPlayer(player));
                System.out.println(consulatPlayer.getMoney());
            }else{
                player.kickPlayer(ChatColor.RED + "Erreur, reconnecte-toi. Si cela persiste, contact un administrateur.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.kickPlayer(ChatColor.RED + "Erreur lors de la récupération de vos données.\n" + e.getMessage());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        try{
            ConsulatPlayer consulatPlayer = PlayersManager.getConsulatPlayer(player);
            if(consulatPlayer != null)
                saveMoney(player);
        }catch(SQLException exception){
            exception.printStackTrace();
        }
    }

    private void saveMoney(Player player) throws SQLException {
        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET money = ? WHERE player_uuid = ?");
        preparedStatement.setDouble(1, PlayersManager.getConsulatPlayer(player).getMoney());
        preparedStatement.setString(2, player.getUniqueId().toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
