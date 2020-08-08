package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.utils.InventoryUtils;
import org.bukkit.Bukkit;

public class OfflineInventoryCommand extends ConsulatCommand {
    
    public OfflineInventoryCommand(){
        super("consulat.api","offinv", "/offinv <joueur>", 0, Rank.ADMIN);
        suggest(Arguments.playerList("joueur"));
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        player.getPlayer().openInventory(InventoryUtils.getOfflineInventory(Bukkit.getOfflinePlayer(args[0]).getUniqueId()));
    }
}
