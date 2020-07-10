package fr.leconsulat.api.inventory;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.ListTag;
import fr.leconsulat.api.nbt.NBTInputStream;
import fr.leconsulat.api.nbt.NBTType;
import fr.leconsulat.api.utils.FileUtils;
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

public class InventoryManager {
    
    private static final InventoryManager instance = new InventoryManager();
    
    private static final Method compoundToItem;
    private static final Method nmsToBukkit;
    private static final Method itemToCompound;
    private static final Method bukkitToNMS;
    
    static{
        try {
            compoundToItem = MinecraftReflection.getMinecraftClass("ItemStack").getDeclaredMethod("a", MinecraftReflection.getNBTCompoundClass());
            nmsToBukkit = MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asBukkitCopy", MinecraftReflection.getItemStackClass());
            itemToCompound = MinecraftReflection.getMinecraftClass("ItemStack").getDeclaredMethod("save", MinecraftReflection.getNBTCompoundClass());
            bukkitToNMS = MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
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
    
    public ListTag<CompoundTag> saveInventory(PlayerInventory inventory){
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
    
    public static InventoryManager getInstance(){
        return instance;
    }
}
