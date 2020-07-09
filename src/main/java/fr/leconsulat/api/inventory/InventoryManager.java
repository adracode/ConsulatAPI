package fr.leconsulat.api.inventory;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.NBTInputStream;
import fr.leconsulat.api.utils.FileUtils;
import fr.leconsulat.api.utils.minecraft.nbt.NBTMinecraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class InventoryManager {
    
    private static final InventoryManager instance = new InventoryManager();
    
    private static final Method compoundToItem;
    private static final Method nmsToBukkit;
    
    static{
        try {
            compoundToItem = MinecraftReflection.getMinecraftClass("ItemStack").getDeclaredMethod("a", MinecraftReflection.getNBTCompoundClass());
            nmsToBukkit = MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asBukkitCopy", MinecraftReflection.getItemStackClass());
        } catch(NoSuchMethodException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    private InventoryManager(){
        if(instance != null){
            throw new IllegalStateException();
        }
    }
    
    public Inventory getOfflineInventory(UUID uuid){
        File playerFile = FileUtils.loadFile(Bukkit.getServer().getWorldContainer(), "world/playerdata/" + uuid + ".dat");
        try {
            Inventory finalInventory = Bukkit.createInventory(null, 54);
            NBTInputStream inputStream = new NBTInputStream(playerFile);
            CompoundTag player = inputStream.read();
            List<CompoundTag> inventory = player.getList("Inventory", CompoundTag.class);
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
    
    public static InventoryManager getInstance(){
        return instance;
    }
}
