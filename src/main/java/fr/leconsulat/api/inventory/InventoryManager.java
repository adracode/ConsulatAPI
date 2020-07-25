package fr.leconsulat.api.inventory;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.ListTag;
import fr.leconsulat.api.nbt.NBTInputStream;
import fr.leconsulat.api.nbt.NBTType;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.utils.FileUtils;
import fr.leconsulat.api.utils.InventoryUtils;
import fr.leconsulat.api.utils.minecraft.nbt.NBTMinecraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
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
    
    private final File playerDataFolder = FileUtils.loadFile(Bukkit.getServer().getWorldContainer(), "world/playerdata/");
    
    private InventoryManager(){
        if(instance != null){
            throw new IllegalStateException();
        }
    }
    
    private File getPlayerFile(UUID uuid){
        return FileUtils.loadFile(playerDataFolder, uuid + ".dat");
    }
    
    public Inventory getOfflineInventory(UUID uuid){
        File playerFile = getPlayerFile(uuid);
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
    
    public String inventoryToString(ConsulatPlayer player){
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(byteStream);
            os.writeObject(InventoryUtils.getInventoryAsTag(player.getPlayer().getInventory()));
            os.close();
            return Base64.getEncoder().encodeToString(byteStream.toByteArray());
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public ListTag<CompoundTag> stringToInventory(String data){
        try {
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
            ListTag<CompoundTag> inventory = (ListTag<CompoundTag>)is.readObject();
            is.close();
            return inventory;
        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static InventoryManager getInstance(){
        return instance;
    }
}
