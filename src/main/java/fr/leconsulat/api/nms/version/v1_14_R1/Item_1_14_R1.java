package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nms.api.Item;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class Item_1_14_R1 implements Item {
    
    @Override
    public String getItemNameId(ItemStack item){
        return CraftItemStack.asNMSCopy(item).getItem().getName();
    }
    
    @Override
    public CompoundTag itemToTag(ItemStack item){
        return ConsulatAPI.getNMS().getNBT().nmsToCompound(CraftItemStack.asNMSCopy(item).save(new NBTTagCompound()));
    }
    
    @Override
    public ItemStack tagToItem(CompoundTag tag){
        return CraftItemStack.asBukkitCopy(net.minecraft.server.v1_14_R1.ItemStack.a((NBTTagCompound)ConsulatAPI.getNMS().getNBT().toNMS(tag)));
    }
}
