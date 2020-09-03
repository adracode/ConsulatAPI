package fr.leconsulat.api.events.items;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerPlaceArmorStandEvent extends PlayerPlaceItemEvent {
    
    public PlayerPlaceArmorStandEvent(Player player, @NotNull Location clickedLocation, @NotNull ItemStack item){
        super(player, clickedLocation, item);
    }
    
}
