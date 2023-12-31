package fr.leconsulat.api.enchantments;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class CEnchantedItem {
    
    private static final Set<Material> ARMORS = EnumSet.of(
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.ELYTRA,
            Material.TURTLE_HELMET
    );
    
    private static final String[] ROMAN = new String[]{"0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    
    private static final byte MAX_ENCHANTS = 2;
    
    private static final EnchantmentDataType DATA_TYPE = new EnchantmentDataType();
    
    private static final NamespacedKey KEY_ENCHANT = new NamespacedKey(ConsulatAPI.getConsulatAPI(), "enchantments");
    private static final NamespacedKey OLD_KEY_ENCHANT = new NamespacedKey("consulatcore", "enchantments");
    private static final NamespacedKey NB_ENCHANT = new NamespacedKey(ConsulatAPI.getConsulatAPI(), "cardinality");
    private static final NamespacedKey OLD_NB_ENCHANT = new NamespacedKey("consulatcore", "cardinality");
    
    private ItemStack handle;
    
    public CEnchantedItem(ItemStack item){
        if(!ARMORS.contains(item.getType()) && item.getType() != Material.ENCHANTED_BOOK){
            throw new IllegalArgumentException("CEnchantment cannot be applied to " + item.getType());
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if(dataContainer.has(OLD_KEY_ENCHANT, PersistentDataType.TAG_CONTAINER)){
            PersistentDataContainer allEnchants = dataContainer.get(OLD_KEY_ENCHANT, PersistentDataType.TAG_CONTAINER);
            dataContainer.remove(OLD_KEY_ENCHANT);
            Byte numberOfEnchant = allEnchants.get(OLD_NB_ENCHANT, PersistentDataType.BYTE);
            if(numberOfEnchant != null){
                allEnchants.remove(OLD_NB_ENCHANT);
                allEnchants.set(NB_ENCHANT, PersistentDataType.BYTE, numberOfEnchant);
                for(byte i = 0; i < numberOfEnchant; ++i){
                    CEnchantment cEnchantment = allEnchants.get(getOldKey(i), DATA_TYPE);
                    if(cEnchantment != null){
                        allEnchants.remove(getOldKey(i));
                        allEnchants.set(getKey(i), DATA_TYPE, cEnchantment);
                    }
                }
            }
            dataContainer.set(KEY_ENCHANT, PersistentDataType.TAG_CONTAINER, allEnchants);
            item.setItemMeta(meta);
        }
        this.handle = item;
    }
    
    public boolean addEnchantment(@NotNull CEnchantment.Type enchantment, int level){
        if(!enchantment.canApply(getSlot(handle.getType()))){
            return false;
        }
        PersistentDataContainer tag = getTag();
        byte numberOfEnchantments = getNumberOfEnchant(tag);
        short index = -1;
        CEnchantment[] currentEnchants = getEnchants();
        for(byte i = 0; i < currentEnchants.length; i++){
            CEnchantment currentEnchant = currentEnchants[i];
            if(currentEnchant.getEnchantment() == enchantment){
                if(currentEnchant.getLevel() > level){
                    return false;
                } else {
                    index = currentEnchant.getLevel() == level ? (short)(i + Byte.MAX_VALUE) : i;
                }
                break;
            }
        }
        if(numberOfEnchantments == MAX_ENCHANTS && index == -1){
            return false;
        }
        if(index >= Byte.MAX_VALUE){
            if(!enchantment.canCombine()){
                return false;
            }
            ++level;
            index -= Byte.MAX_VALUE;
        }
        if(level > enchantment.getMaxLevel()){
            return false;
        }
        addEnchantment(tag, index != -1 ? (byte)(index) : numberOfEnchantments, enchantment, level);
        if(index == -1){
            tag.set(NB_ENCHANT, PersistentDataType.BYTE, ++numberOfEnchantments);
        }
        ItemMeta meta = handle.getItemMeta();
        if(handle.getType() != Material.ENCHANTED_BOOK && !meta.hasEnchants()){
            if(!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
                meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        } else if(meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) && meta.getEnchants().size() > 1 && meta.hasEnchant(Enchantment.ARROW_INFINITE) && meta.getEnchantLevel(Enchantment.ARROW_INFINITE) == 0){
            meta.removeEnchant(Enchantment.ARROW_INFINITE);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        List<String> description = meta.getLore();
        if(description == null){
            description = new ArrayList<>();
        }
        if(index != -1 && index < description.size()){
            description.set(index, "§7" + enchantment.getDisplay() + " " + ROMAN[level]);
        } else {
            description.add(numberOfEnchantments - 1, "§7" + enchantment.getDisplay() + " " + (level <= 10 ? ROMAN[level] : level));
        }
        meta.setLore(description);
        meta.getPersistentDataContainer().set(KEY_ENCHANT, PersistentDataType.TAG_CONTAINER, tag);
        handle.setItemMeta(meta);
        return true;
    }
    
    public boolean isEnchantedWith(CEnchantment.Type enchant){
        PersistentDataContainer tag = getTag();
        byte size = getNumberOfEnchant(tag);
        for(byte i = 0; i < size; ++i){
            if(tag.get(getKey(i), DATA_TYPE).getEnchantment() == enchant){
                return true;
            }
        }
        return false;
    }
    
    public boolean isEnchantedWith(CEnchantment.Type enchant, int level){
        PersistentDataContainer tag = getTag();
        byte size = getNumberOfEnchant(tag);
        for(byte i = 0; i < size; ++i){
            CEnchantment enchantment = tag.get(getKey(i), DATA_TYPE);
            if(enchantment.getEnchantment() == enchant && enchantment.getLevel() == level){
                return true;
            }
        }
        return false;
    }
    
    public void removeEnchants(){
        ItemMeta meta = handle.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        List<String> description = meta.getLore();
        if(description == null){
            ConsulatAPI.getConsulatAPI().log(Level.WARNING, "Enchanted item without lore");
            return;
        }
        for(byte i = 0, size = getNumberOfEnchant(getTag()); i < size; ++i){
            description.remove(0);
        }
        data.remove(KEY_ENCHANT);
        meta.setLore(description);
        handle.setItemMeta(meta);
    }
    
    public ItemStack getHandle(){
        return handle;
    }
    
    public Material getType(){
        return handle.getType();
    }
    
    public CEnchantment[] getEnchants(){
        PersistentDataContainer tag = getTag();
        byte size = getNumberOfEnchant(tag);
        CEnchantment[] enchants = new CEnchantment[size];
        for(byte i = 0; i < size; ++i){
            enchants[i] = tag.get(getKey(i), DATA_TYPE);
        }
        return enchants;
    }
    
    private PersistentDataContainer getTag(){
        ItemMeta meta = handle.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        PersistentDataContainer allEnchantments = dataContainer.get(KEY_ENCHANT, PersistentDataType.TAG_CONTAINER);
        return allEnchantments == null ? dataContainer.getAdapterContext().newPersistentDataContainer() : allEnchantments;
    }
    
    private byte getNumberOfEnchant(PersistentDataContainer tag){
        Byte numberOfEnchant = tag.get(NB_ENCHANT, PersistentDataType.BYTE);
        return numberOfEnchant == null ? 0 : numberOfEnchant;
    }
    
    private void addEnchantment(PersistentDataContainer tag, byte index, CEnchantment.Type type, int level){
        tag.set(getKey(index), DATA_TYPE, new CEnchantment(type, level));
    }
    
    public static  @NotNull EquipmentSlot getSlot(Material material){
        switch(material){
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case GOLDEN_HELMET:
            case DIAMOND_HELMET:
            case TURTLE_HELMET:
                return EquipmentSlot.HEAD;
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case ELYTRA:
                return EquipmentSlot.CHEST;
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case DIAMOND_LEGGINGS:
                return EquipmentSlot.LEGS;
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case DIAMOND_BOOTS:
                return EquipmentSlot.FEET;
            case ENCHANTED_BOOK:
                return EquipmentSlot.HAND;
        }
        return EquipmentSlot.OFF_HAND;
    }
    
    public static CEnchantedItem getItem(ItemStack item){
        if(item == null){
            return null;
        }
        if(!ARMORS.contains(item.getType()) && item.getType() != Material.ENCHANTED_BOOK){
            return null;
        }
        return isEnchanted(item) ? new CEnchantedItem(item) : null;
    }
    
    public static boolean isEnchanted(@Nullable ItemStack item){
        return item != null && item.hasItemMeta() &&
                (item.getItemMeta().getPersistentDataContainer().has(KEY_ENCHANT, PersistentDataType.TAG_CONTAINER) ||
                item.getItemMeta().getPersistentDataContainer().has(OLD_KEY_ENCHANT, PersistentDataType.TAG_CONTAINER));
    }
    
    private static NamespacedKey getOldKey(int i){
        return new NamespacedKey("consulatcore", Integer.toString(i));
    }
    
    private static NamespacedKey getKey(int i){
        return new NamespacedKey(ConsulatAPI.getConsulatAPI(), Integer.toString(i));
    }
    
    @Override
    public String toString(){
        return "CEnchantedItem{" +
                "handle=" + handle +
                '}';
    }
}
