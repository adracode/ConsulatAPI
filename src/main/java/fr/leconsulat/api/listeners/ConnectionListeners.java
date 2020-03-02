package fr.leconsulat.api.listeners;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.player.PlayersManager;
import fr.leconsulat.api.ranks.RankDatabase;
import fr.leconsulat.api.ranks.RankEnum;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionListeners implements Listener {

    private RankDatabase rankDatabase;

    public ConnectionListeners(RankDatabase rankDatabase) {
        this.rankDatabase = rankDatabase;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        RankEnum playerRank;
        int id;
        try {
            PlayersManager.initializePlayer(player, PlayersManager.fetchPlayer(player));
            ConsulatPlayer consulatPlayer = PlayersManager.getConsulatPlayer(player);

            System.out.println(consulatPlayer.getMoney());
        } catch (SQLException e) {
            e.printStackTrace();
            player.kickPlayer(ChatColor.RED + "Erreur lors de la récupération de vos données.\n" + e.getMessage());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        try{
            saveMoney(player);
        }catch(SQLException exception){
            exception.printStackTrace();
        }
    }

    private void saveMoney(Player player) throws SQLException {
        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET money = ? WHERE player_name = ?");
        preparedStatement.setDouble(1, PlayersManager.getConsulatPlayer(player).getMoney());
        preparedStatement.setString(2, player.getName());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
