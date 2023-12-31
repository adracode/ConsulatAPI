package fr.leconsulat.api.enchantments;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnchantmentManager implements Listener {
    
    private static final EnchantmentManager instance = new EnchantmentManager();
    
    private final Map<UUID, ItemStack> anvilEventCalled = new HashMap<>();
    
    private EnchantmentManager(){
        ConsulatAPI.getConsulatAPI().getServer().getPluginManager().registerEvents(this, ConsulatAPI.getConsulatAPI());
    }
    
    public void applyCEnchantment(ConsulatPlayer player, CEnchantment... armorEnchants){
        Player bukkitPlayer = player.getPlayer();
        for(CEnchantment enchant : armorEnchants){
            PotionEffectType effect = enchant.getEnchantment().getEffect();
            PotionEffect currentEffect = bukkitPlayer.getPotionEffect(effect);
            if(currentEffect != null){
                if(currentEffect.getAmplifier() > enchant.getLevel()){
                    continue;
                }
                if(currentEffect.getAmplifier() < enchant.getLevel()){
                    bukkitPlayer.removePotionEffect(currentEffect.getType());
                }
            }
            bukkitPlayer.addPotionEffect(new PotionEffect(enchant.getEnchantment().getEffect(), Integer.MAX_VALUE, enchant.getLevel() - 1, false, false));
        }
    }
    
    public boolean isSimilar(@Nullable ItemStack stack1, @Nullable ItemStack stack2){
        if(stack1 == null && stack2 == null){
            return true;
        }
        if(stack1 == null || stack2 == null){
            return false;
        }
        boolean b = stack1.getType() == stack2.getType() && stack1.hasItemMeta() == stack2.hasItemMeta();
        if(!b){
            return false;
        }
        return stack1.getEnchantments().equals(stack2.getEnchantments()) &&
                stack1.getItemMeta().getPersistentDataContainer().equals(stack2.getItemMeta().getPersistentDataContainer());
    }
    
    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event){
        if(!CEnchantedItem.isEnchanted(event.getOldItem()) && !CEnchantedItem.isEnchanted(event.getNewItem())){
            return;
        }
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId());
        PlayerArmorChangeEvent.SlotType slot = event.getSlotType();
        CEnchantedItem oldArmor = CEnchantedItem.isEnchanted(event.getOldItem()) ? new CEnchantedItem(event.getOldItem()) : null;
        Player bukkitPlayer = player.getPlayer();
        if(oldArmor != null){
            CEnchantment[] armorEnchants = oldArmor.getEnchants();
            ItemStack[] armor = bukkitPlayer.getInventory().getArmorContents();
            //Si une autre partie de l'armure comporte cet enchantement, on ne l'enlève pas
            for(CEnchantment enchant : armorEnchants){
                boolean removeEnchant = true;
                for(int i = 0; i < 4; ++i){
                    if(i != slot.ordinal()){
                        CEnchantedItem armorPart = CEnchantedItem.getItem(armor[i]);
                        if(armorPart != null && armorPart.isEnchantedWith(enchant.getEnchantment(), enchant.getLevel())){
                            removeEnchant = false;
                            break;
                        }
                    }
                }
                if(removeEnchant){
                    PotionEffectType effect = enchant.getEnchantment().getEffect();
                    PotionEffect currentEffect = bukkitPlayer.getPotionEffect(effect);
                    if(currentEffect == null || currentEffect.getAmplifier() <= enchant.getLevel()){
                        bukkitPlayer.removePotionEffect(enchant.getEnchantment().getEffect());
                    }
                }
            }
        }
        CEnchantedItem newArmor = CEnchantedItem.isEnchanted(event.getNewItem()) ? new CEnchantedItem(event.getNewItem()) : null;
        if(newArmor != null){
            applyCEnchantment(player, newArmor.getEnchants());
        }
    }
    
    @EventHandler
    public void applyCEnchantment(EntityPotionEffectEvent event){
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        PotionEffect oldEffect = event.getOldEffect();
        if(oldEffect == null || oldEffect.getAmplifier() == 0){
            return;
        }
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getEntity().getUniqueId());
        if(player == null){
            return;
        }
        ItemStack[] armor = player.getPlayer().getInventory().getArmorContents();
        for(int i = 0; i < 4; ++i){
            CEnchantedItem armorPart = CEnchantedItem.getItem(armor[i]);
            if(armorPart == null){
                continue;
            }
            for(CEnchantment enchantment : armorPart.getEnchants()){
                if(oldEffect.getType().equals(enchantment.getEnchantment().getEffect())){
                    Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () -> {
                        player.getPlayer().addPotionEffect(new PotionEffect(enchantment.getEnchantment().getEffect(), Integer.MAX_VALUE, enchantment.getLevel() - 1, false, false));
                    });
                    break;
                }
            }
        }
    }
    
    @EventHandler
    public void onItemCombineInAnvil(PrepareAnvilEvent event){
        if(anvilEventCalled.containsKey(event.getView().getPlayer().getUniqueId())){
            event.setResult(anvilEventCalled.get(event.getView().getPlayer().getUniqueId()));
            return;
        }
        ItemStack result = event.getResult();
        ItemStack second = event.getInventory().getItem(1);
        if(CEnchantedItem.isEnchanted(second)){
            ItemStack first = event.getInventory().getItem(0);
            if(first != null && first.getType() != Material.AIR){
                if(first.getType() == Material.ENCHANTED_BOOK && second.getType() != Material.ENCHANTED_BOOK){
                    return;
                }
                int extraCost = 0;
                if(result == null || result.getType() == Material.AIR){
                    result = first.clone();
                }
                CEnchantedItem resultEnchanted = new CEnchantedItem(result);
                CEnchantedItem secondEnchanted = new CEnchantedItem(second);
                for(CEnchantment secondEnchantment : secondEnchanted.getEnchants()){
                    if(!resultEnchanted.addEnchantment(secondEnchantment.getEnchantment(), secondEnchantment.getLevel())){
                        cancelAnvil(event);
                        return;
                    }
                    extraCost += 10;
                }
                if(CEnchantedItem.isEnchanted(result) && hasMending(result)){
                    cancelAnvil(event);
                    return;
                }
                event.setResult(result);
                int finalExtraCost = extraCost;
                Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                    event.getInventory().setRepairCost(event.getInventory().getRepairCost() + finalExtraCost);
                });
            }
        }
        if(CEnchantedItem.isEnchanted(result) && hasMending(result)){
            cancelAnvil(event);
            return;
        }
        if(result != null && result.getType() != Material.AIR && result.getEnchantments().size() > 1 && result.containsEnchantment(Enchantment.ARROW_INFINITE) && CEnchantedItem.isEnchanted(result)){
            ItemMeta meta = result.getItemMeta();
            meta.removeEnchant(Enchantment.ARROW_INFINITE);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            result.setItemMeta(meta);
        }
        anvilEventCalled.put(event.getView().getPlayer().getUniqueId(), event.getResult());
    }
    
    @EventHandler
    public void onGrindstone(InventoryClickEvent event){
        if(!(event.getWhoClicked().getOpenInventory().getTopInventory() instanceof GrindstoneInventory)){
            return;
        }
        Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
            if(!(event.getWhoClicked().getOpenInventory().getTopInventory() instanceof GrindstoneInventory)){
                return;
            }
            GrindstoneInventory inventory = (GrindstoneInventory)event.getWhoClicked().getOpenInventory().getTopInventory();
            ItemStack result = inventory.getItem(2);
            if(CEnchantedItem.isEnchanted(result)){
                new CEnchantedItem(result).removeEnchants();
            }
        });
    }
    
    @EventHandler
    public void onTick(ServerTickEndEvent event){
        if(!anvilEventCalled.isEmpty()){
            anvilEventCalled.clear();
        }
    }
    
    private boolean hasMending(ItemStack item){
        if(item == null || item.getType() == Material.AIR){
            return false;
        }
        if(item.getType() == Material.ENCHANTED_BOOK){
            EnchantmentStorageMeta book = (EnchantmentStorageMeta)item.getItemMeta();
            return book.hasStoredEnchant(Enchantment.MENDING);
        }
        return item.containsEnchantment(Enchantment.MENDING);
    }
    
    private void cancelAnvil(PrepareAnvilEvent event){
        event.setResult(null);
        Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
            event.getInventory().setRepairCost(0);
            ((Player)event.getView().getPlayer()).updateInventory();
        });
    }
    
    public static EnchantmentManager getInstance(){
        return instance;
    }
}
