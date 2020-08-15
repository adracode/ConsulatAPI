package fr.leconsulat.api.nms.api;

import fr.leconsulat.api.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Packet {
    
    void openSignEditor(Player player, Location location);
    
    void setSlot(Player player, int slot, ItemStack item);
    
    void tileEntityData(Player player, Location location, int action, CompoundTag compoundTag);
}
