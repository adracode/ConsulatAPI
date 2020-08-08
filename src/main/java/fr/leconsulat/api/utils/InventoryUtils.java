package fr.leconsulat.api.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.*;
import fr.leconsulat.api.utils.minecraft.NMSUtils;
import fr.leconsulat.api.utils.minecraft.nbt.NBTMinecraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class InventoryUtils {
    
    private static final Method itemToCompound;
    private static final Method bukkitToNMS;
    private static final Method compoundToItem;
    private static final Method nmsToBukkit;
    
    static{
        try {
            itemToCompound = MinecraftReflection.getMinecraftClass("ItemStack").getDeclaredMethod("save", MinecraftReflection.getNBTCompoundClass());
            bukkitToNMS = MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
            compoundToItem = MinecraftReflection.getMinecraftClass("ItemStack").getDeclaredMethod("a", MinecraftReflection.getNBTCompoundClass());
            nmsToBukkit = MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asBukkitCopy", MinecraftReflection.getItemStackClass());
        } catch(NoSuchMethodException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static ListTag<CompoundTag> getInventoryAsTag(PlayerInventory inventory){
        ListTag<CompoundTag> listTag = new ListTag<>(NBTType.COMPOUND);
        try {
            for(int i = 0; i < inventory.getSize(); ++i){
                ItemStack itemStack = inventory.getItem(i);
                if(itemStack != null){
                    CompoundTag tag = NBTMinecraft.nmsToCompound(
                            itemToCompound.invoke(bukkitToNMS.invoke(null, itemStack), NBTMinecraft.newCompoundTag()));
                    tag.putByte("Slot", (byte)i);
                    listTag.addTag(tag);
                }
            }
            ItemStack[] armor = inventory.getArmorContents();
            for(int i = 0; i < armor.length; ++i){
                if(armor[i] != null){
                    CompoundTag tag = NBTMinecraft.nmsToCompound(
                            itemToCompound.invoke(bukkitToNMS.invoke(null, armor[i]), NBTMinecraft.newCompoundTag()));
                    tag.putByte("Slot", (byte)(i + 100));
                    listTag.addTag(tag);
                }
            }
            ItemStack[] extra = inventory.getExtraContents();
            for(int i = 0; i < extra.length; ++i){
                if(extra[i] != null){
                    CompoundTag tag = NBTMinecraft.nmsToCompound(
                            itemToCompound.invoke(bukkitToNMS.invoke(null, extra[i]), NBTMinecraft.newCompoundTag()));
                    tag.putByte("Slot", (byte)(i + 150));
                    listTag.addTag(tag);
                }
            }
            return listTag;
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<CompoundTag> readInventoryFromFile(UUID uuid){
        File playerFile = ConsulatAPI.getConsulatAPI().getPlayerFile(uuid);
        try {
            return new NBTInputStream(playerFile).read().getList("Inventory", NBTType.COMPOUND);
        } catch(IOException  e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static Inventory getOfflineInventory(UUID uuid){
        File playerFile = ConsulatAPI.getConsulatAPI().getPlayerFile(uuid);
        try {
            Inventory finalInventory = Bukkit.createInventory(null, 54);
            NBTInputStream inputStream = new NBTInputStream(playerFile);
            CompoundTag player = inputStream.read();
            List<CompoundTag> inventory = player.getList("Inventory", NBTType.COMPOUND);
            for(CompoundTag tag : inventory){
                int slot = tag.getByte("Slot") & 255;
                ItemStack itemstack = (ItemStack)nmsToBukkit.invoke(null, compoundToItem.invoke(null, NBTMinecraft.compoundToNMS(tag)));
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
        } catch(IOException | IllegalAccessException | InvocationTargetException e){
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
    
    public static ItemStack fromTag(CompoundTag tag){
        return NMSUtils.nmsToBukkitItem(NMSUtils.compoundToItem(NBTMinecraft.compoundToNMS(tag)));
    }
    
}
