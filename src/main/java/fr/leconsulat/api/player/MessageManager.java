package fr.leconsulat.api.player;


import fr.leconsulat.api.ranks.RankEnum;
import org.bukkit.Bukkit;

public class MessageManager {

    /**
     * Envoie le message qu'à un grade
     * @param rankEnum
     * @param message
     */
    public static void sendMessageTo(RankEnum rankEnum, String message){
        Bukkit.getOnlinePlayers().forEach(player -> {
            ConsulatPlayer consulatPlayer = PlayersManager.getConsulatPlayer(player);
            if(consulatPlayer.getRank().equals(rankEnum)){
                player.sendMessage(message);
            }
        });
    }

    /**
     * Envoie un message au grade concerné et supérieur
     * @param rankEnum
     * @param message
     */
    public static void sendMessageHigher(RankEnum rankEnum, String message){
        Bukkit.getOnlinePlayers().forEach(player -> {
            ConsulatPlayer consulatPlayer = PlayersManager.getConsulatPlayer(player);
            if(consulatPlayer.getRank().getRankPower() >= rankEnum.getRankPower()){
                player.sendMessage(message);
            }
        });
    }
}
