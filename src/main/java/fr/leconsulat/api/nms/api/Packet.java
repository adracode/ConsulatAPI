package fr.leconsulat.api.nms.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Packet {
    
    void setSlot(Player player, int slot, ItemStack item);
}
