package fr.leconsulat.api.player;

import fr.leconsulat.api.custom.CustomObject;
import fr.leconsulat.api.ranks.RankEnum;

public class ConsulatPlayer {

    private RankEnum playerRank;
    private int idPlayer;
    private boolean hasPerso;
    private String persoPrefix;

    public ConsulatPlayer(RankEnum playerRank, int id, CustomObject customObject) {
        this.playerRank = playerRank;
        this.idPlayer = id;
        this.hasPerso = customObject.isBuyed();
        this.persoPrefix = customObject.getPrefix();
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

    public boolean isPerso() {
        return hasPerso;
    }

    public String getPersoPrefix() {
        return persoPrefix;
    }

    public void setHasPerso(boolean hasPerso) {
        this.hasPerso = hasPerso;
    }

    public void setPersoPrefix(String persoPrefix) {
        this.persoPrefix = persoPrefix;
    }
}
