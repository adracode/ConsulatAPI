package fr.leconsulat.api.commands;

import fr.leconsulat.api.inventory.InventoryManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.player.stream.PlayerOutputStream;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.redis.RedisManager;
import fr.leconsulat.api.utils.InventoryUtils;
import org.bukkit.Bukkit;

public class OfflineInventoryCommand extends ConsulatCommand {
    
    public OfflineInventoryCommand(){
        super("offinv", "/offinv <joueur>", 0, Rank.DEVELOPPEUR);
        suggest(false, Arguments.playerList("joueur"));
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        InventoryManager inventoryManager = InventoryManager.getInstance();
        if(args.length == 0){
            System.out.println(
                    InventoryUtils.getInventoryAsTag(player.getPlayer().getInventory()));
            return;
        }
        if(args[0].equalsIgnoreCase("save")){
            RedisManager.getInstance().getRedis().getTopic("SavePlayerDataSurvie").publish(
                    new PlayerOutputStream(player.getPlayer()).writeInventory().send());
            return;
        }
        player.getPlayer().openInventory(inventoryManager.getOfflineInventory(Bukkit.getOfflinePlayer(args[0]).getUniqueId()));
    }
}
