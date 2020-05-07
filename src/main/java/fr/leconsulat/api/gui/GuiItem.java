package fr.leconsulat.api.gui;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.CPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GuiItem extends ItemStack implements Cloneable {
    
    private String permission;
    private byte slot;
    private Object attachedObject;
    
    public GuiItem(GuiItem item){
        super(item);
        this.permission = item.permission;
        this.slot = item.slot;
        this.attachedObject = item.attachedObject;
    }
    
    public GuiItem(String name, byte slot, Material material){
        this(name, slot, material, null);
    }
    
    public GuiItem(String name, byte slot, Material material, List<String> description){
        this(material, name, description);
        this.slot = slot;
    }
    
    public GuiItem(String name, byte slot, String player, List<String> description){
        this(name, player, description);
        this.slot = slot;
    }
    
    private GuiItem(Material material){
        super(material);
    }
    
    private GuiItem(Material material, String name, List<String> description){
        this(material);
        ItemMeta meta = getItemMeta();
        if(name != null && !name.isEmpty()){
            meta.setDisplayName(name);
        }
        if(description != null && description.size() != 0){
            meta.setLore(description);
        }
        setItemMeta(meta);
    }
    
    public GuiItem(ItemStack item, int slot){
        super(item);
        this.slot = (byte)slot;
    }
    
    private GuiItem(String name, String player, List<String> description){
        this(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)getItemMeta();
        meta.setDisplayName(name);
        if(description != null){
            meta.setLore(description);
        }
        UUID uuid = CPlayerManager.getInstance().getPlayerUUID(player);
        if(uuid == null){
            Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(player));
                setItemMeta(meta);
            });
        } else {
            Player bukkitPlayer = Bukkit.getPlayer(uuid);
            meta.setOwningPlayer(bukkitPlayer == null ? Bukkit.getOfflinePlayer(uuid) : bukkitPlayer);
        }
        setItemMeta(meta);
    }
    
    public GuiItem setGlowing(boolean b){
        ItemMeta meta = getItemMeta();
        if(b){
            meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
            if(!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        } else {
            meta.removeEnchant(Enchantment.ARROW_INFINITE);
        }
        setItemMeta(meta);
        return this;
    }
    
    public byte getSlot(){
        return slot;
    }
    
    public String getDisplayName(){
        if(hasItemMeta() && getItemMeta().hasDisplayName()){
            return getItemMeta().getDisplayName();
        }
        return "";
    }
    
    public GuiItem setDisplayName(String displayName){
        ItemMeta meta = getItemMeta();
        if(displayName == null || displayName.isEmpty()){
            meta.setDisplayName(null);
        } else {
            meta.setDisplayName(displayName);
        }
        setItemMeta(meta);
        return this;
    }
    
    public List<String> getDescription(){
        if(hasItemMeta()){
            return getItemMeta().getLore();
        }
        return Collections.emptyList();
    }
    
    public GuiItem setDescription(String... description){
        ItemMeta meta = getItemMeta();
        meta.setLore(Arrays.asList(description));
        setItemMeta(meta);
        return this;
    }
    
    public String getPermission(){
        return permission;
    }
    
    public GuiItem setPermission(String permission){
        this.permission = permission;
        return this;
    }
    
    GuiItem setSlot(int slot){
        this.slot = (byte)slot;
        return this;
    }
    
    public GuiItem get(byte slot){
        return new GuiItem(this).setSlot(slot);
    }
    
    public Object getAttachedObject(){
        return attachedObject;
    }
    
    public void setAttachedObject(Object attachedObject){
        this.attachedObject = attachedObject;
    }
    
    @Override
    public GuiItem clone(){
        return new GuiItem(this);
    }
    
    @Override
    public String toString(){
        return "GuiItem{" +
                super.toString() +
                ", permission='" + permission + '\'' +
                ", slot=" + slot +
                ", attachedObject=" + attachedObject +
                '}';
    }
}
