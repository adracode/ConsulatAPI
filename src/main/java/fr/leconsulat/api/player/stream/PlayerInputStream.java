package fr.leconsulat.api.player.stream;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nms.api.Item;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public class PlayerInputStream extends OfflinePlayerInputStream {
    
    private Player player;
    
    public PlayerInputStream(Player player, byte[] data){
        super(data);
        this.player = player;
        UUID uuid = fetchUUID();
        if(!player.getUniqueId().equals(uuid)){
            close();
            throw new IllegalArgumentException("Bad player, given " + player.getUniqueId() + ", received " + uuid);
        }
    }
    
    public PlayerInputStream readFully(){
        readActiveEffects().readHealth().readFood().readSaturation().readExhaustion().readFoodTickTimer().readLevel().readInventory();
        return this;
    }
    
    public PlayerInputStream readHealth(){
        try {
            float health = fetchHealth();
            if(health < 0){
                health = 0F;
            }
            player.setHealth(health);
        } catch(IllegalArgumentException e){
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }
        return this;
    }
    
    public PlayerInputStream readFood(){
        player.setFoodLevel(fetchFood());
        return this;
    }
    
    public PlayerInputStream readSaturation(){
        player.setSaturation(fetchSaturation());
        return this;
    }
    
    public PlayerInputStream readExhaustion(){
        player.setExhaustion(fetchExhaustion());
        return this;
    }
    
    public PlayerInputStream readFoodTickTimer(){
        ConsulatAPI.getNMS().getPlayer().setFoodTickTimer(player, fetchFoodTickTimer());
        return this;
    }
    
    public PlayerInputStream readLevel(){
        float experience = fetchLevel();
        int level = (int)experience;
        player.setLevel(level);
        player.setExp(experience - level);
        return this;
    }
    
    public PlayerInputStream readInventory(){
        Item itemNMS = ConsulatAPI.getNMS().getItem();
        List<CompoundTag> inventoryTag = fetchInventory().getValue();
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        ItemStack[] armor = new ItemStack[4];
        ItemStack[] extra = new ItemStack[1];
        for(CompoundTag tag : inventoryTag){
            int slot = tag.getByte("Slot") & 255;
            ItemStack itemstack = itemNMS.tagToItem(tag);
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
    
    public PlayerInputStream readActiveEffects(){
        List<CompoundTag> effects = fetchActiveEffects().getValue();
        for(PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }
        fr.leconsulat.api.nms.api.Player playerNMS = ConsulatAPI.getNMS().getPlayer();
        for(CompoundTag effect : effects){
            playerNMS.addEffect(player, effect);
        }
        return this;
    }
    
}
