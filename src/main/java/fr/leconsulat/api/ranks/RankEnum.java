package fr.leconsulat.api.ranks;

import org.bukkit.ChatColor;

public enum RankEnum {

    ADMIN("Admin", ChatColor.RED, 100, 100),
    RESPONSABLE("Responsable", ChatColor.GOLD, 95, 100),
    MODPLUS("Superviseur", ChatColor.YELLOW, 90, 100),
    MODO("Modérateur", ChatColor.YELLOW, 50, 95),
    DEVELOPPEUR("Développeur", ChatColor.BLUE, 30, 100),
    BUILDER("Builder", ChatColor.BLUE, 30, 100),
    MECENE("Mécène", ChatColor.GREEN, 15, 95),
    FINANCEUR("Financeur", ChatColor.DARK_GREEN, 5, 95),
    TOURISTE("Touriste", ChatColor.GRAY, 0, 95),
    JOUEUR("Joueur", ChatColor.GRAY, 0, 95),
    INVITE("Invité", ChatColor.WHITE, 0, 100);

    private String rankName;
    private ChatColor rankColor;
    private int rankPower;
    private int minPower;

    RankEnum(String rankName, ChatColor rankColor, int rankPower, int minPower) {
        this.rankName = rankName;
        this.rankColor = rankColor;
        this.rankPower = rankPower;
        this.minPower = minPower;
    }

    public String getRankName() {
        return rankName;
    }

    public ChatColor getRankColor() {
        return rankColor;
    }

    public int getRankPower() {
        return rankPower;
    }

    public int getMinPower() {
        return minPower;
    }
}
