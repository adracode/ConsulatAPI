package fr.leconsulat.api.player;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.ranks.RankEnum;
import fr.leconsulat.api.ranks.RankManager;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PlayersManager {

    private static Map<Player, ConsulatPlayer> consulatPlayers = new HashMap<Player, ConsulatPlayer>();

    public static ConsulatPlayer getConsulatPlayer(Player player){ return consulatPlayers.get(player);  }
    public static boolean isInitialized(Player player) { return consulatPlayers.containsKey(player); }
    public static void initializePlayer(Player player, ConsulatPlayer consulatPlayer){ consulatPlayers.put(player, consulatPlayer); }

    public static ConsulatPlayer fetchPlayer(Player player) throws SQLException {
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("SELECT player_rank, id FROM players WHERE player_name = ?");
        request.setString(1, player.getName());
        ResultSet resultSet = request.executeQuery();
        if(resultSet.next()){
            RankEnum playerRank = RankManager.getRankByName(resultSet.getString("player_rank"));
            int id = resultSet.getInt("id");
            return new ConsulatPlayer(playerRank, id);
        }else{
            return new ConsulatPlayer(RankEnum.INVITE, 0);
        }
    }
}