package fr.leconsulat.api.ranks;

import org.bukkit.ChatColor;

public enum RankEnum {

    ADMIN("Admin", ChatColor.RED, 100, 100),
    RESPONSABLE("Responsable", ChatColor.GOLD, 90, 100),
    MODO("Modérateur", ChatColor.YELLOW, 50, 100),
    DEVELOPPEUR("Développeur", ChatColor.BLUE, 30, 100),
    BUILDER("Builder", ChatColor.BLUE, 30, 100),
    ANIMATEUR("Animateur", ChatColor.BLUE, 15, 100),
    MECENE("Mécène", ChatColor.GREEN, 15, 90),
    FINANCEUR("Financeur", ChatColor.DARK_GREEN, 5, 90),
    JOUEUR("Joueur", ChatColor.GRAY, 0, 100),
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
