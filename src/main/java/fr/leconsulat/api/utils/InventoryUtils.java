package fr.leconsulat.api.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.nbt.*;
import fr.leconsulat.api.utils.minecraft.NMSUtils;
import fr.leconsulat.api.utils.minecraft.nbt.NBTMinecraft;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class InventoryUtils {
    
    private static final Method itemToCompound;
    private static final Method bukkitToNMS;
    
    static{
        try {
            itemToCompound = MinecraftReflection.getMinecraftClass("ItemStack").getDeclaredMethod("save", MinecraftReflection.getNBTCompoundClass());
            bukkitToNMS = MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
        } catch(NoSuchMethodException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    private static final File playerDataFolder = FileUtils.loadFile(Bukkit.getServer().getWorldContainer(), "world/playerdata/");
   
    private static File getPlayerFile(UUID uuid){
        return FileUtils.loadFile(playerDataFolder, uuid + ".dat");
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
    
    public static void writeInventoryToFile(UUID uuid, ListTag<CompoundTag> tag){
        try {
            File playerFile = getPlayerFile(uuid);
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
