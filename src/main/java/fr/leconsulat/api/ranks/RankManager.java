package fr.leconsulat.api.ranks;

import fr.leconsulat.api.player.PlayersManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;

public class RankManager {

    private RankDatabase rankDatabase;

    public RankManager(RankDatabase rankDatabase) {
        this.rankDatabase = rankDatabase;
    }

    public static RankEnum getRankByName(String name){
        return Arrays.stream(RankEnum.values()).filter(rank -> rank.getRankName().equalsIgnoreCase(name)).findFirst().orElse(RankEnum.INVITE);
    }

    public RankEnum getRank(Player player){
        return PlayersManager.getConsulatPlayer(player).getRank();
    }

    public boolean changeRank(Player target, RankEnum newRank){
        PlayersManager.getConsulatPlayer(target).setRank(newRank);
        try {
            rankDatabase.changeRank(target, newRank);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}