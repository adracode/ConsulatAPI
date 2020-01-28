package fr.leconsulat.api.player;

import fr.leconsulat.api.ranks.RankEnum;

public class OfflineConsulat {

    private int id;
    private String player_uuid;
    private String player_name;
    private RankEnum player_rank;
    private String date_registered;
    private Double money;
    private int more_homes;
    private int shops;

    public OfflineConsulat(int id, String player_uuid, String player_name, RankEnum player_rank, String date_registered, Double money, int more_homes, int shops) {
        this.id = id;
        this.player_uuid = player_uuid;
        this.player_name = player_name;
        this.player_rank = player_rank;
        this.date_registered = date_registered;
        this.money = money;
        this.more_homes = more_homes;
        this.shops = shops;
    }

    public int getId() {
        return id;
    }

    public String getPlayer_uuid() {
        return player_uuid;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public RankEnum getPlayer_rank() {
        return player_rank;
    }

    public String getDate_registered() {
        return date_registered;
    }

    public Double getMoney() {
        return money;
    }

    public int getMore_homes() {
        return more_homes;
    }

    public int getShops() {
        return shops;
    }
}
