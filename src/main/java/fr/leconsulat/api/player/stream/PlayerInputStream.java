package fr.leconsulat.api.player.stream;

import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.UUID;

public class PlayerInputStream extends OfflinePlayerInputStream {
    
    private Player player;
    
    public PlayerInputStream(Player player, byte[] data){
        super(data);
        this.player = player;
        UUID uuid = fetchUUID();
        if(player.getUniqueId() != uuid){
            close();
            throw new IllegalArgumentException("Bad player, given " + player.getUniqueId() + ", received " + uuid);
        }
    }
    
    public PlayerInputStream readLevel(){
        float experience = fetchLevel();
        int level = (int)experience;
        player.setLevel(level);
        player.setExp(experience - level);
        return this;
    }
    
    public PlayerInputStream readInventory(){
        List<CompoundTag> inventoryTag = fetchInventory().getValue();
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        ItemStack[] armor = new ItemStack[4];
        ItemStack[] extra = new ItemStack[1];
        for(CompoundTag tag : inventoryTag){
            int slot = tag.getByte("Slot") & 255;
            ItemStack itemstack = InventoryUtils.fromTag(tag);
            if(itemstack.getType() != Material.AIR){
                if(slot < inventory.getSize()){
                    inventory.setItem(slot, itemstack);
                } else if(slot >= 100 && slot < 4 + 100){
                    armor[slot - 100] = itemstack;
                } else if(slot >= 150 && slot < 1 + 150){
                    extra[slot - 150] = itemstack;
                }
            }
        }
        inventory.setArmorContents(armor);
        inventory.setExtraContents(extra);
        return this;
    }
    
}
