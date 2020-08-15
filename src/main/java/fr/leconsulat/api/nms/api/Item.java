package fr.leconsulat.api.nms.api;

import fr.leconsulat.api.nbt.CompoundTag;
import org.bukkit.inventory.ItemStack;

public interface Item {
    
    String getItemNameId(ItemStack item);
    
    CompoundTag itemToTag(ItemStack item);
    
    ItemStack tagToItem(CompoundTag tag);
    
}
