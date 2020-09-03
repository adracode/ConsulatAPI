package fr.leconsulat.api.utils;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.*;
import fr.leconsulat.api.nms.api.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class InventoryUtils {

    public static ListTag<CompoundTag> getInventoryAsTag(PlayerInventory inventory){
        Item itemNMS = ConsulatAPI.getNMS().getItem();
        ListTag<CompoundTag> listTag = new ListTag<>(NBTType.COMPOUND);
        for(int i = 0; i < inventory.getSize(); ++i){
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack != null){
                CompoundTag tag = itemNMS.itemToTag(itemStack);
                tag.putByte("Slot", (byte)i);
                listTag.addTag(tag);
            }
        }
        ItemStack[] armor = inventory.getArmorContents();
        for(int i = 0; i < armor.length; ++i){
            if(armor[i] != null){
                CompoundTag tag = itemNMS.itemToTag(armor[i]);
                tag.putByte("Slot", (byte)(i + 100));
                listTag.addTag(tag);
            }
        }
        ItemStack[] extra = inventory.getExtraContents();
        for(int i = 0; i < extra.length; ++i){
            if(extra[i] != null){
                CompoundTag tag = itemNMS.itemToTag(extra[i]);
                tag.putByte("Slot", (byte)(i + 150));
                listTag.addTag(tag);
            }
        }
        return listTag;
    }
    
    public static List<CompoundTag> readInventoryFromFile(UUID uuid){
        File playerFile = ConsulatAPI.getConsulatAPI().getPlayerFile(uuid);
        try {
            return new NBTInputStream(playerFile).read().getList("Inventory", NBTType.COMPOUND);
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static Inventory getOfflineInventory(UUID uuid){
        Item itemNMS = ConsulatAPI.getNMS().getItem();
        File playerFile = ConsulatAPI.getConsulatAPI().getPlayerFile(uuid);
        try {
            Inventory finalInventory = Bukkit.createInventory(null, 54);
            NBTInputStream inputStream = new NBTInputStream(playerFile);
            CompoundTag player = inputStream.read();
            List<CompoundTag> inventory = player.getList("Inventory", NBTType.COMPOUND);
            for(CompoundTag tag : inventory){
                int slot = tag.getByte("Slot") & 255;
                ItemStack itemstack = itemNMS.tagToItem(tag);
                if(itemstack.getType() != Material.AIR){
                    if(slot < finalInventory.getSize()){
                        finalInventory.setItem(slot, itemstack);
                    } else if(slot >= 100 && slot < 4 + 100){
                        finalInventory.setItem(slot - 100 + 36, itemstack);
                    } else if(slot >= 150 && slot < 1 + 150){
                        finalInventory.setItem(slot - 150 + 45, itemstack);
                    }
                }
            }
            return finalInventory;
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static void writeInventoryToFile(UUID uuid, ListTag<CompoundTag> tag){
        try {
            File playerFile = ConsulatAPI.getConsulatAPI().getPlayerFile(uuid);
            NBTInputStream is = new NBTInputStream(playerFile);
            CompoundTag player = is.read();
            is.close();
            player.put("Inventory", tag);
            NBTOutputStream os = new NBTOutputStream(playerFile, player);
            os.write("");
            os.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
