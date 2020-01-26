package fr.leconsulat.api.ranks;


import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RankDatabase {

    private Connection connection;

    public RankDatabase() {
        this.connection = ConsulatAPI.getDatabase();
    }

    void changeRank(Player player, RankEnum newRank) throws SQLException {
        PreparedStatement request = connection.prepareStatement("UPDATE players SET player_rank = ? WHERE player_name = ?");
        request.setString(1, newRank.getRankName());
        request.setString(2, player.getName());
        request.executeUpdate();
    }
}
