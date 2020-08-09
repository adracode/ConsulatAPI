package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nms.api.Item;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class Item_1_14_R1 implements Item {
    
    @Override
    public String getItemNameId(ItemStack item){
        return CraftItemStack.asNMSCopy(item).getItem().getName();
    }
}
