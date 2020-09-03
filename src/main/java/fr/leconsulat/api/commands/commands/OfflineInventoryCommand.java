package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OfflineInventoryCommand extends ConsulatCommand {
    
    public OfflineInventoryCommand(){
        super(ConsulatAPI.getConsulatAPI(),"offinv");
        setUsage("/offinv <joueur>").setArgsMin(1).suggest(Arguments.playerList("joueur"));
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer player, @NotNull String[] args){
        try {
            player.getPlayer().openInventory(Objects.requireNonNull(InventoryUtils.getOfflineInventory(Bukkit.getOfflinePlayer(args[0]).getUniqueId())));
        } catch(NullPointerException e){
            player.sendMessage("§cErreur.");
            e.printStackTrace();
        }
    }
}
