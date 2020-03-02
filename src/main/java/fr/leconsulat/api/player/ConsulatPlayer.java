package fr.leconsulat.api.player;

import fr.leconsulat.api.claim.ClaimObject;
import fr.leconsulat.api.custom.CustomObject;
import fr.leconsulat.api.ranks.RankEnum;

public class ConsulatPlayer {

    private RankEnum playerRank;
    private int idPlayer;
    private boolean hasPerso;
    private String persoPrefix;
    private Double money;

    public ClaimObject claimedChunk;

    public ConsulatPlayer(RankEnum playerRank, int id, CustomObject customObject, Double money) {
        this.playerRank = playerRank;
        this.idPlayer = id;
        this.hasPerso = customObject.isBuyed();
        this.persoPrefix = customObject.getPrefix();
        this.money = money;
    }

    public RankEnum getRank() {
        return playerRank;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money)  {
        this.money = money;
    }

    public void addMoney(Double amount) {
        this.money += amount;
    }

    public void removeMoney(Double amount) {
        this.money = this.money - amount;
    }

    public boolean canRemove(Double amount) {
        return !((this.money - amount) < 0);
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
