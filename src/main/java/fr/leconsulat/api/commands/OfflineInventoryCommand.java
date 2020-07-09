package fr.leconsulat.api.commands;

import fr.leconsulat.api.inventory.InventoryManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.bukkit.Bukkit;

public class OfflineInventoryCommand extends ConsulatCommand {
    
    public OfflineInventoryCommand(){
        super("offinv", "/offinv <joueur>", 1, Rank.DEVELOPPEUR);
        suggest(false, Arguments.playerList("joueur"));
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        player.getPlayer().openInventory(InventoryManager.getInstance().getOfflineInventory(Bukkit.getOfflinePlayer(args[0]).getUniqueId()));
    }
}
