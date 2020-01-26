package fr.leconsulat.api.player;

import fr.leconsulat.api.ranks.RankEnum;

public class ConsulatPlayer {

    private RankEnum playerRank;
    private int idPlayer;
    
    public ConsulatPlayer(RankEnum playerRank, int id) {
        this.playerRank = playerRank;
        this.idPlayer = id;
    }

    public RankEnum getRank() {
        return playerRank;
    }

    public void setRank(RankEnum playerRank) {
        this.playerRank = playerRank;
    }

    public int getIdPlayer() {
        return idPlayer;
    }
}
