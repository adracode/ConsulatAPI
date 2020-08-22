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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuiItem extends ItemStack implements Cloneable {
    
    public static final ItemStack AIR = new ItemStack(Material.AIR);
    public static final GuiItem BACK = new GuiItem("§cRetour", (byte)-1, Material.RED_STAINED_GLASS_PANE);
    
    private byte slot;
    private @Nullable Object attachedObject;
    private @Nullable Map<UUID, ItemStack> fakeItems;
    
    private GuiItem(Material material){
        super(material);
        setFlags();
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
    
    public GuiItem(GuiItem item){
        super(item);
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
    
    public GuiItem(String name, byte slot, UUID player, List<String> description){
        this(name, player, description);
        this.slot = slot;
    }
    
    public GuiItem(ItemStack item, int slot){
        super(item);
        setFlags();
        this.slot = (byte)slot;
    }
    
    public GuiItem(String name, UUID uuid, List<String> description){
        this(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)getItemMeta();
        meta.setDisplayName(name);
        if(description != null){
            meta.setLore(description);
        }
        Player bukkitPlayer = Bukkit.getPlayer(uuid);
        meta.setOwningPlayer(bukkitPlayer == null ? Bukkit.getOfflinePlayer(uuid) : bukkitPlayer);
        setItemMeta(meta);
    }
    
    public List<String> getDescription(String... add){
        List<String> description = null;
        if(hasItemMeta()){
            description = getItemMeta().getLore();
        }
        if(description == null){
            description = new ArrayList<>();
        }
        description.addAll(Arrays.asList(add));
        return description;
    }
    
    public GuiItem get(byte slot){
        return new GuiItem(this).setSlot(slot);
    }
    
    public ItemStack getFakeItem(UUID uuid){
        return containsFakeItems() ? fakeItems.get(uuid) : null;
    }
    
    public void addFakeItem(UUID uuid, ItemStack item){
        if(fakeItems == null){
            fakeItems = new HashMap<>();
        }
        fakeItems.put(uuid, item);
    }
    
    public boolean removeFakeItem(UUID uuid){
        if(!containsFakeItems()){
            return false;
        }
        return fakeItems.remove(uuid) != null;
    }
    
    public void clearFakeItems(UUID uuid){
        if(fakeItems != null){
            fakeItems.remove(uuid);
        }
    }
    
    public boolean containsFakeItems(){
        return fakeItems != null && !fakeItems.isEmpty();
    }
    
    private void setFlags(){
        this.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_POTION_EFFECTS
        );
    }
    
    public byte getSlot(){
        return slot;
    }
    
    public GuiItem setSlot(int slot){
        this.slot = (byte)slot;
        return this;
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
            List<String> lore = getItemMeta().getLore();
            return lore == null ? new ArrayList<>() : lore;
        }
        return new ArrayList<>();
    }
    
    public GuiItem setDescription(String... description){
        return setDescription(Arrays.asList(description));
    }
    
    public GuiItem setDescription(List<String> description){
        ItemMeta meta = getItemMeta();
        meta.setLore(description);
        setItemMeta(meta);
        return this;
    }
    
    public @Nullable Object getAttachedObject(){
        return attachedObject;
    }
    
    public void setAttachedObject(@NotNull Object attachedObject){
        this.attachedObject = attachedObject;
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
    
    @Override
    public String toString(){
        return "GuiItem{" +
                super.toString() +
                ", slot=" + slot +
                ", attachedObject=" + attachedObject +
                '}';
    }
    
    @Override
    public GuiItem clone(){
        return new GuiItem(this);
    }
    
    public static boolean isGuiItem(ItemStack item){
        if(item == null || item.getType() == Material.AIR){
            return false;
        }
        return item.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) && item.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
    }
    
    public static List<String> getDescription(ItemStack item, String... add){
        List<String> description = null;
        if(item.hasItemMeta()){
            description = item.getItemMeta().getLore();
        }
        if(description == null){
            description = new ArrayList<>();
        }
        description.addAll(Arrays.asList(add));
        return description;
    }
}
