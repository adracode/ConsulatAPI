package fr.leconsulat.api.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AGuiItem implements Cloneable {
    
    private ItemStack item;
    private String permission;
    private byte slot;
    
    public AGuiItem(AGuiItem item){
        this.item = new ItemStack(item.item);
        this.permission = item.permission;
        this.slot = item.slot;
    }
    
    public AGuiItem(String name, byte slot, Material material){
        this(name, slot, material, null);
    }
    
    
    public AGuiItem(String name, byte slot, Material material, List<String> description){
        this.item = ItemBuilder.getItem(material, name, description);
        this.slot = slot;
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);
        if(meta.hasDisplayName() && meta.getDisplayName().isEmpty()){
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        }
        item.setItemMeta(meta);
    }
    
    public AGuiItem(String name, byte slot, String player, List<String> description){
        this(ItemBuilder.getHead(name, player, description), slot);
    }
    
    public AGuiItem(ItemStack item, byte slot){
        this.item = item;
        this.slot = slot;
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);
        if(meta.hasDisplayName() && meta.getDisplayName().isEmpty()){
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        }
        item.setItemMeta(meta);
    }
    
    public AGuiItem setGlowing(boolean b){
        ItemMeta meta = item.getItemMeta();
        if(b){
            meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
            if(!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        } else {
            meta.removeEnchant(Enchantment.ARROW_INFINITE);
        }
        item.setItemMeta(meta);
        return this;
    }
    
    public ItemStack getItem(){
        return item;
    }
    
    public void setItem(ItemStack item){
        this.item = item;
    }
    
    public byte getSlot(){
        return slot;
    }
    
    public String getDisplayName(){
        if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
            return item.getItemMeta().getDisplayName();
        }
        return "";
    }
    
    public AGuiItem setDisplayName(String displayName){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return this;
    }
    
    public List<String> getDescription(){
        if(item.hasItemMeta()){
            return item.getItemMeta().getLore();
        }
        return Collections.emptyList();
    }
    
    public AGuiItem setDescription(String... description){
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(description));
        item.setItemMeta(meta);
        return this;
    }
    
    public Material getType(){
        return item.getType();
    }
    
    public AGuiItem setType(Material type){
        item.setType(type);
        return this;
    }
    
    public String getPermission(){
        return permission;
    }
    
    public AGuiItem setPermission(String permission){
        this.permission = permission;
        return this;
    }
    
    AGuiItem setSlot(int slot){
        this.slot = (byte)slot;
        return this;
    }
    
    public AGuiItem get(byte slot){
        return duplicate().setSlot(slot);
    }
    
    public AGuiItem duplicate(){
        try {
            return (AGuiItem)super.clone();
        } catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return null;
    }
}
