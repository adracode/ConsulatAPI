package fr.leconsulat.api.gui;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.CPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class ItemBuilder {
    
    public static ItemStack getItem(Material material){
        return new ItemStack(material);
    }
    
    public static ItemStack getItem(Material material, String name, List<String> description){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if(description != null && description.size() != 0){
            meta.setLore(description);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack getHead(String name, String player, List<String> description){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setDisplayName(name);
        if(description != null){
            meta.setLore(description);
        }
        UUID uuid = CPlayerManager.getInstance().getPlayerUUID(player);
        if(uuid == null){
            Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(player));
                item.setItemMeta(meta);
            });
        } else {
            Player bukkitPlayer = Bukkit.getPlayer(uuid);
            meta.setOwningPlayer(bukkitPlayer == null ? Bukkit.getOfflinePlayer(uuid) : bukkitPlayer);
        }
        item.setItemMeta(meta);
        return item;
    }
}
