package fr.leconsulat.api.utils;

import org.jnbt.Tag;

import java.util.Map;

public class NBTUtils {

    /* Item */
    /*
    public static ItemStack itemFromNBT(CompoundTag itemTag){
        Map<String, Tag> itemMap = itemTag.getValue();
        ItemStack item = ItemBuilder.getItem(
                getChildTag(itemMap, "Type", ShortTag.class).getValue(),
                getChildTag(itemMap, "Damage", ShortTag.class).getValue(),
                getChildTag(itemMap, "Count", ByteTag.class).getValue());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getChildTag(itemMap, "Name", StringTag.class).getValue());
        for(Tag enchantTag : getChildTag(itemMap, "Enchants", ListTag.class).getValue()){
            Map<String, Tag> map = ((CompoundTag)enchantTag).getValue();
            meta.addEnchant(
                    Enchantment.getById(getChildTag(map, "Type", ByteTag.class).getValue()),
                    getChildTag(map, "Level", ShortTag.class).getValue(), true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static CompoundTag itemToNBT(byte slot, ItemStack item){
        return itemToNBT("", slot, item);
    }

    public static CompoundTag itemToNBT(String name, byte slot, ItemStack item){
        Map<String, Tag> itemMap = new HashMap<>();
        itemMap.put("type", new ShortTag("Type", (short) item.getType().getId()));
        itemMap.put("durability", new ShortTag("Damage", item.getDurability()));
        itemMap.put("count", new ByteTag("Count", (byte) item.getAmount()));
        itemMap.put("slot", new ByteTag("Slot", slot));
        ItemMeta meta = item.getItemMeta();
        itemMap.put("name", new StringTag("Name", meta.hasDisplayName() ? meta.getDisplayName() : ""));
        List<Tag> enchants = new ArrayList<>();
        for(Map.Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet()){
            Map<String, Tag> enchantMap = new HashMap<>();
            enchantMap.put("id", new ByteTag("Type", (byte) enchant.getKey().getId()));
            enchantMap.put("lvl", new ShortTag("Level", (short)(int)enchant.getValue()));
            enchants.add(new CompoundTag("", enchantMap));
        }
        itemMap.put("enchants", new ListTag("Enchants", CompoundTag.class, enchants));
        return new CompoundTag(name, itemMap);
    }

    /* Chest */
    /*
    public static void loadChestFromNBT(STEChest chest, CompoundTag tag){
        List<Tag> itemList = NBTUtils.getChildTag(tag.getValue(), "Items", ListTag.class).getValue();
        for(Tag itemTag : itemList){
            Map<String, Tag> itemMap = ((CompoundTag) itemTag).getValue();
            chest.addItem(
                    getChildTag(itemMap, "Slot", ByteTag.class).getValue(),
                    itemFromNBT((CompoundTag) itemTag));
        }
    }

    public static CompoundTag chestToNBT(Inventory chest, Material chestType, int x, int y, int z){
        List<Tag> list = new ArrayList<>(chest.getSize());
        for(byte slot = 0; slot < chest.getSize(); slot++){
            ItemStack item = chest.getItem(slot);
            if(item != null && item.getType() != Material.AIR){
                list.add(NBTUtils.itemToNBT(slot, item));
            }
        }
        Map<String, Tag> tileEntityMap = new HashMap<>();
        tileEntityMap.put("type", new ByteTag("Type", (byte) chestType.getId()));
        tileEntityMap.put("x", new IntTag("x", x));
        tileEntityMap.put("y", new IntTag("y", y));
        tileEntityMap.put("z", new IntTag("z", z));
        tileEntityMap.put("items", new ListTag("Items", CompoundTag.class, list));
        return new CompoundTag("", tileEntityMap);
    }

    /* Sign */
    /*
    public static void loadSignFromNBT(STESign sign, CompoundTag tag){
        List<Tag> lines = getChildTag(tag.getValue(), "Lines", ListTag.class).getValue();
        for(Tag lineTag : lines){
            sign.addLine(((StringTag) lineTag).getValue());
        }

    }

    public static CompoundTag signToNBT(Sign sign, Material type, int x, int y, int z){
        List<Tag> lines = new ArrayList<>();
        String[] line = sign.getLines();
        for(int i = 0; i < line.length; i++){
            lines.add(new StringTag(Integer.toString(i), line[i]));
        }
        Map<String, Tag> tileEntityMap = new HashMap<>();
        tileEntityMap.put("type", new ByteTag("Type", (byte) type.getId()));
        tileEntityMap.put("x", new IntTag("x", x));
        tileEntityMap.put("y", new IntTag("y", y));
        tileEntityMap.put("z", new IntTag("z", z));
        tileEntityMap.put("items", new ListTag("Lines", StringTag.class, lines));
        return new CompoundTag("", tileEntityMap);
    }

    /* Location */
    /*
    public static Location loadLocationFromNBT(CompoundTag tag, World world){
        Map<String, Tag> map = tag.getValue();
        return new Location(world,
                getChildTag(map, "x", FloatTag.class).getValue(),
                getChildTag(map, "y", FloatTag.class).getValue(),
                getChildTag(map, "z", FloatTag.class).getValue(),
                getChildTag(map, "Yaw", FloatTag.class).getValue(),
                getChildTag(map, "Pitch", FloatTag.class).getValue()
        );
    }
    
    public static CompoundTag locationToNBT(Location location, String name){
        Map<String, Tag> map = new HashMap<>();
        map.put("x", new FloatTag("x", (float)location.getX()));
        map.put("y", new FloatTag("y", (float)location.getY()));
        map.put("z", new FloatTag("z", (float)location.getZ()));
        map.put("yaw", new FloatTag("Yaw", location.getYaw()));
        map.put("pitch", new FloatTag("Pitch", location.getPitch()));
        return new CompoundTag(name, map);
    }
    */
    public static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException{
        if(!items.containsKey(key)){
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if(!expected.isInstance(tag)){
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}
