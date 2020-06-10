package fr.leconsulat.api.events;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.events.blocks.*;
import fr.leconsulat.api.events.entities.*;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffectType;

public class EventManager implements Listener {
    
    public static EventManager instance;
    
    public EventManager(){
        if(instance != null){
            return;
        }
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, ConsulatAPI.getConsulatAPI());
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        switch(event.getAction()){
            case RIGHT_CLICK_BLOCK:{
                if(clickedBlock == null){
                    return;
                }
                if(player.isSneaking() && (player.getInventory().getItemInMainHand().getType() != Material.AIR || player.getInventory().getItemInOffHand().getType() != Material.AIR)){
                    return;
                }
                PType type = PType.getCategory(clickedBlock.getType());
                if(type == null){
                    return;
                }
                PlayerInteractBlockEvent interactBlockEvent = null;
                switch(type){
                    case BUTTON:
                        interactBlockEvent = new PlayerInteractButtonEvent(event.getClickedBlock(), player);
                        break;
                    case DOOR:
                        interactBlockEvent = new PlayerInteractDoorEvent(event.getClickedBlock(), player);
                        break;
                    case FENCE:
                        interactBlockEvent = new PlayerInteractFenceEvent(event.getClickedBlock(), player);
                        break;
                    case FENCE_GATE:
                        interactBlockEvent = new PlayerInteractFenceGateEvent(event.getClickedBlock(), player);
                        break;
                    case SIGN:
                        interactBlockEvent = new PlayerInteractSignEvent(event.getClickedBlock(), player);
                        break;
                    case TRAPDOOR:
                        interactBlockEvent = new PlayerInteractTrapdoorEvent(event.getClickedBlock(), player);
                        break;
                    case ANVIL:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.ANVIL);
                        break;
                    case BEACON:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.BEACON);
                        break;
                    case CARTOGRAPHY_TABLE:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.CARTOGRAPHY_TABLE);
                        break;
                    case CRAFTING_TABLE:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.CRAFTING_TABLE);
                        break;
                    case ENCHANTING_TABLE:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.ENCHANTING_TABLE);
                        break;
                    case ENDER_CHEST:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.ENDER_CHEST);
                        break;
                    case FLETCHING_TABLE:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.FLETCHING_TABLE);
                        break;
                    case GRINDSTONE:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.GRINDSTONE);
                        break;
                    case LOOM:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.LOOM);
                        break;
                    case SMITHING_TABLE:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.SMITHING_TABLE);
                        break;
                    case STONECUTTER:
                        interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, PlayerInteractGuiBlockEvent.Type.STONECUTTER);
                        break;
                    case BARREL:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.BARREL);
                        break;
                    case BLAST_FURNACE:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.BLAST_FURNACE);
                        break;
                    case BREWING_STAND:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.BREWING_STAND);
                        break;
                    case CHEST:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.CHEST);
                        break;
                    case DISPENSER:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.DISPENSER);
                        break;
                    case DROPPER:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.DROPPER);
                        break;
                    case FURNACE:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.FURNACE);
                        break;
                    case HOPPER:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.HOPPER);
                        break;
                    case SHULKER_BOX:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.SHULKER_BOX);
                        break;
                    case SMOKER:
                        interactBlockEvent = new PlayerInteractContentBlockEvent(event.getClickedBlock(), player, PlayerInteractContentBlockEvent.Type.SMOKER);
                        break;
                    case BELL:
                        interactBlockEvent = new PlayerInteractBellEvent(event.getClickedBlock(), player);
                        break;
                    case BED:
                        interactBlockEvent = new PlayerInteractBedEvent(event.getClickedBlock(), player);
                        break;
                    case CAKE:
                        interactBlockEvent = new PlayerInteractCakeEvent(event.getClickedBlock(), player);
                        break;
                    case CAMPFIRE:
                        interactBlockEvent = new PlayerInteractCampfireEvent(event.getClickedBlock(), player);
                        break;
                    case CAULDRON:
                        interactBlockEvent = new PlayerInteractCauldronEvent(event.getClickedBlock(), player);
                        break;
                    case OPERATOR:
                        interactBlockEvent = new PlayerInteractForbiddenEvent(event.getClickedBlock(), player);
                        break;
                    case COMPARATOR:
                    case DAYLIGHT_DETECTOR:
                    case REPEATER:
                        interactBlockEvent = new PlayerInteractRedstoneComponentEvent(event.getClickedBlock(), player);
                        break;
                    case COMPOSTER:
                        interactBlockEvent = new PlayerInteractComposterEvent(event.getClickedBlock(), player);
                        break;
                    case DRAGON_EGG:
                        interactBlockEvent = new PlayerInteractDragonEggEvent(event.getClickedBlock(), player);
                        break;
                    case FLOWER_POT:
                        interactBlockEvent = new PlayerInteractFlowerPotEvent(event.getClickedBlock(), player);
                        break;
                    case JUKEBOX:
                        interactBlockEvent = new PlayerInteractJukeboxEvent(event.getClickedBlock(), player);
                        break;
                    case LECTERN:
                        interactBlockEvent = new PlayerInteractLecternEvent(event.getClickedBlock(), player);
                        break;
                    case LEVER:
                        interactBlockEvent = new PlayerInteractLeverEvent(event.getClickedBlock(), player);
                        break;
                    case NOTE_BLOCK:
                        interactBlockEvent = new PlayerInteractNoteEvent(event.getClickedBlock(), player);
                        break;
                    case PUMPKIN:
                        interactBlockEvent = new PlayerInteractPumpkinEvent(event.getClickedBlock(), player);
                        break;
                    case REDSTONE_ORE:
                        interactBlockEvent = new PlayerInteractRedstoneOreEvent(event.getClickedBlock(), player);
                        break;
                    case SWEET_BERRY_BUSH:
                        interactBlockEvent = new PlayerInteractBerryEvent(event.getClickedBlock(), player);
                        break;
                    case TNT:
                        interactBlockEvent = new PlayerInteractTntEvent(event.getClickedBlock(), player);
                        break;
                    case PRESSURE_PLATE:
                        interactBlockEvent = new PlayerInteractPlateEvent(event.getClickedBlock(), player);
                        break;
                    case FARMLAND:
                        interactBlockEvent = new PlayerInteractFarmlandEvent(event.getClickedBlock(), player);
                        break;
                    case TRIPWIRE:
                        interactBlockEvent = new PlayerInteractTripwireEvent(event.getClickedBlock(), player);
                        break;
                    case TURTLE_EGG:
                        interactBlockEvent = new PlayerInteractTurtleEggEvent(event.getClickedBlock(), player);
                        break;
                    case NOTHING:
                        return;
                }
                Bukkit.getPluginManager().callEvent(interactBlockEvent);
                if(interactBlockEvent.isCancelled()){
                    event.setCancelled(true);
                }
    
            }
        }
    }
    
    //TODO: leash
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent interactEvent){
        Entity entity = interactEvent.getRightClicked();
        Player player = interactEvent.getPlayer();
        ItemStack itemInHand = player.getInventory().getItem(interactEvent.getHand());
        Material material = itemInHand == null ? Material.AIR : itemInHand.getType();
        Event event = null;
        switch(material){
            case NAME_TAG:
                if(entity.getType() != EntityType.PLAYER && !itemInHand.getItemMeta().hasDisplayName()){
                    event = new PlayerNameEntityEvent(entity, player);
                }
                break;
            case BLACK_DYE:
            case BLUE_DYE:
            case BROWN_DYE:
            case CYAN_DYE:
            case GRAY_DYE:
            case GREEN_DYE:
            case LIGHT_BLUE_DYE:
            case LIGHT_GRAY_DYE:
            case LIME_DYE:
            case MAGENTA_DYE:
            case ORANGE_DYE:
            case PINK_DYE:
            case PURPLE_DYE:
            case RED_DYE:
            case WHITE_DYE:
            case YELLOW_DYE:
                if(entity.getType() == EntityType.SHEEP && !((Sheep)entity).isSheared() && ((Colorable)entity).getColor() != getColor(itemInHand.getType())){
                    event = new PlayerDyeSheepEvent(entity, player);
                }
                break;
            case SADDLE:
                if(entity.getType() == EntityType.PIG && ((Ageable)entity).isAdult() && !((Pig)entity).hasSaddle()){
                    event = new PlayerSaddlePigEvent(entity, player);
                }
                break;
        }
        
        if(event == null){
            switch(entity.getType()){
                case DONKEY:
                case LLAMA:
                case MULE:
                case TRADER_LLAMA:
                case HORSE:
                    if(((Ageable)entity).isAdult() && player.isSneaking() && ((Tameable)entity).isTamed()){
                        event = new PlayerOpenEntityInventoryEvent(entity, player);
                    } else if(entity.getType() != EntityType.HORSE && material == Material.CHEST && !((ChestedHorse)entity).isCarryingChest()){
                        event = new PlayerApplyChestEvent(entity, player);
                    } else if(material == Material.SADDLE &&
                            ((Tameable)entity).isTamed() &&
                            ((Ageable)entity).isAdult() &&
                            ((Horse)entity).getInventory().getSaddle() == null){
                        event = new PlayerOpenEntityInventoryEvent(entity, player);
                    }
                    break;
                case SKELETON_HORSE:
                case ZOMBIE_HORSE:
                    if(!((Tameable)entity).isTamed() || !((Ageable)entity).isAdult()){
                        return;
                    }
                    if(player.isSneaking()){
                        event = new PlayerOpenEntityInventoryEvent(entity, player);
                    } else if(entity.getPassengers().size() > 0){
                        return;
                    } else if(material == Material.SADDLE){
                        event = new PlayerOpenEntityInventoryEvent(entity, player);
                    }
                    break;
                case TROPICAL_FISH:
                case PUFFERFISH:
                case COD:
                case SALMON:
                    if(material == Material.BUCKET){
                        event = new PlayerPickupFishEvent(entity, player);
                    }
                    break;
                case CAT:
                    if(((Tameable)entity).isTamed() && ((Tameable)entity).getOwnerUniqueId() == player.getUniqueId()){
                        DyeColor color = getColor(material);
                        if(color != null && ((Cat)entity).getCollarColor() != color){
                            event = new PlayerChangeCollarColorEvent(entity, player);
                        } else if(canEatItem(entity.getType(), itemInHand) && ((LivingEntity)entity).getHealth() < ((LivingEntity)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()){
                            event = new PlayerHealTamedAnimalEvent(entity, player);
                        }
                    }
                    break;
                case WOLF://TODO
                    if(((Tameable)entity).isTamed()){
                        if(isMeet(material) && ((LivingEntity)entity).getHealth() < ((LivingEntity)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()){
                            event = new PlayerHealTamedAnimalEvent(entity, player);
                        }
                    }
                    break;
                case MUSHROOM_COW:
                    if(((Ageable)entity).isAdult()){
                        if(material == Material.BOWL){
                            event = new PlayerFillBowlEvent(entity, player);
                        }
                    }
                case COW:
                    if(((Ageable)entity).isAdult()){
                        if(material == Material.BUCKET){
                            event = new PlayerFillBucketMilkEvent(entity, player);
                        }
                    }
                    break;
                case CREEPER:
                    if(material == Material.FLINT_AND_STEEL){
                        event = new PlayerLightCreeperEvent(entity, player);
                    }
                    break;
                case DOLPHIN:
                    if(isFish(material)){
                        event = new PlayerGivesFishDaulphinEvent(entity, player);
                    }
                    break;
                case OCELOT:
                case PANDA:
                case PIG:
                case RABBIT:
                case TURTLE:
                case CHICKEN:
                    //Handled after
                    break;
                case PARROT:
                    break;
                case SHEEP:
                    DyeColor color = getColor(material);
                    if(color != null && !((Sheep)entity).isSheared() && ((Colorable)entity).getColor() != color){
                        event = new PlayerDyeSheepEvent(entity, player);
                    }
                    break;
                case ZOMBIE_VILLAGER:
                    if(material == Material.GOLDEN_APPLE && (((LivingEntity)entity).hasPotionEffect(PotionEffectType.WEAKNESS))){
                        event = new PlayerHealZombieVillagerEvent(entity, player);
                    }
                    break;
                case BOAT:
                    //Handled by VehicleEvent
                    break;
                case MINECART_CHEST:
                case MINECART_HOPPER:
                    event = new PlayerOpenMinecartInventoryEvent(entity, player);
                    break;
                case MINECART_FURNACE:
                    if(isCoal(material)){
                        event = new PlayerFillFuelEvent(entity, player);
                    }
                    break;
                case ITEM_FRAME:
                    if(((ItemFrame)entity).getItem().getType() != Material.AIR){
                        event = new PlayerTurnItemFrameEvent(entity, player);
                    } else if(material != Material.AIR){
                        event = new PlayerPutInItemFrameEvent(entity, player);
                    }
                    break;
                case LEASH_HITCH:
                    //TODO
                    break;
                case MINECART_COMMAND:
                    event = new PlayerOpenCommandBlockEvent(entity, player);
                    break;
                case PLAYER:
                case BAT:
                case EGG:
                case FOX:
                case VEX:
                case HUSK:
                case ARROW:
                case BLAZE:
                case GHAST:
                case GIANT:
                case SLIME:
                case SQUID:
                case STRAY:
                case WITCH:
                case EVOKER:
                case SPIDER:
                case WITHER:
                case ZOMBIE:
                case DROWNED:
                case PHANTOM:
                case RAVAGER:
                case SHULKER:
                case SNOWMAN:
                case TRIDENT:
                case UNKNOWN:
                case ENDERMAN:
                case FIREBALL:
                case FIREWORK:
                case GUARDIAN:
                case MINECART://Handled by entering in vehicle
                case PAINTING:
                case PILLAGER:
                case SKELETON:
                case SNOWBALL:
                case VILLAGER:
                case ENDERMITE:
                case LIGHTNING:
                case ILLUSIONER:
                case IRON_GOLEM://Iron in 1.15
                case LLAMA_SPIT:
                case MAGMA_CUBE:
                case PIG_ZOMBIE:
                case POLAR_BEAR:
                case PRIMED_TNT:
                case SILVERFISH:
                case VINDICATOR:
                case ARMOR_STAND://Handled by ArmorManipulateEvent
                case CAVE_SPIDER:
                case ENDER_PEARL:
                case DROPPED_ITEM:
                case ENDER_DRAGON:
                case ENDER_SIGNAL:
                case EVOKER_FANGS:
                case FISHING_HOOK:
                case MINECART_TNT:
                case WITHER_SKULL:
                case ENDER_CRYSTAL:
                case FALLING_BLOCK:
                case SPLASH_POTION:
                case ELDER_GUARDIAN:
                case EXPERIENCE_ORB:
                case SHULKER_BULLET:
                case SMALL_FIREBALL:
                case SPECTRAL_ARROW:
                case DRAGON_FIREBALL:
                case WITHER_SKELETON:
                case WANDERING_TRADER:
                case AREA_EFFECT_CLOUD:
                case THROWN_EXP_BOTTLE:
                case MINECART_MOB_SPAWNER:
                    break;
            }
        }
        if(event == null && entity instanceof Animals){
            if(canEatItem(entity.getType(), itemInHand)){
                event = new PlayerFeedEntityEvent(entity, player);
            }
        }
        if(event != null){
            Bukkit.getPluginManager().callEvent(event);
            if(((Cancellable)event).isCancelled()){
                interactEvent.setCancelled(true);
            }
        }
    }
    
    private boolean isCoal(Material material){
        switch(material){
            case COAL:
            case CHARCOAL:
                return true;
        }
        return false;
    }
    
    private boolean isFish(Material material){
        switch(material){
            case COD:
            case COOKED_COD:
            case SALMON:
            case COOKED_SALMON:
            case TROPICAL_FISH:
            case PUFFERFISH:
                return true;
            default:
                return false;
        }
    }
    
    private boolean canEatItem(EntityType entityType, ItemStack itemStack){
        if(itemStack == null){
            return false;
        }
        switch(entityType){
            case DONKEY:
            case HORSE:
            case MULE:
            case SKELETON_HORSE:
            case ZOMBIE_HORSE:
                switch(itemStack.getType()){
                    case WHEAT:
                    case SUGAR:
                    case HAY_BLOCK:
                    case APPLE:
                    case GOLDEN_CARROT:
                    case GOLDEN_APPLE:
                    case ENCHANTED_GOLDEN_APPLE:
                        return true;
                }
            case LLAMA:
            case TRADER_LLAMA:
                switch(itemStack.getType()){
                    case WHEAT:
                    case HAY_BLOCK:
                        return true;
                }
            case CAT:
            case OCELOT:
                switch(itemStack.getType()){
                    case SALMON:
                    case COD:
                        return true;
                }
            case WOLF:
                return isMeet(itemStack.getType());
            case COW:
            case MUSHROOM_COW:
                return itemStack.getType() == Material.WHEAT;
            case PANDA:
                return itemStack.getType() == Material.BAMBOO;
            case PIG:
                switch(itemStack.getType()){
                    case CARROT:
                    case POTATO:
                    case BEETROOT:
                        return true;
                }
            case CHICKEN:
                switch(itemStack.getType()){
                    case WHEAT_SEEDS:
                    case MELON_SEEDS:
                    case PUMPKIN_SEEDS:
                    case BEETROOT_SEEDS:
                        return true;
                }
            case FOX:
                return itemStack.getType() == Material.SWEET_BERRIES;
            case RABBIT:
                switch(itemStack.getType()){
                    case CARROT:
                    case GOLDEN_CARROT:
                    case DANDELION:
                        return true;
                }
            case TURTLE:
                return itemStack.getType() == Material.SEAGRASS;
        }
        return false;
    }
    
    private boolean isMeet(Material material){
        switch(material){
            case CHICKEN:
            case COOKED_CHICKEN:
            case PORKCHOP:
            case BEEF:
            case RABBIT:
            case COOKED_PORKCHOP:
            case COOKED_BEEF:
            case ROTTEN_FLESH:
            case MUTTON:
            case COOKED_MUTTON:
            case COOKED_RABBIT:
                return true;
            default:
                return false;
        }
    }
    
    private DyeColor getColor(Material material){
        switch(material){
            case BLACK_DYE:
                return DyeColor.BLACK;
            case BLUE_DYE:
                return DyeColor.BLUE;
            case BROWN_DYE:
                return DyeColor.BROWN;
            case CYAN_DYE:
                return DyeColor.CYAN;
            case GRAY_DYE:
                return DyeColor.GRAY;
            case GREEN_DYE:
                return DyeColor.GREEN;
            case LIGHT_BLUE_DYE:
                return DyeColor.LIGHT_BLUE;
            case LIGHT_GRAY_DYE:
                return DyeColor.LIGHT_GRAY;
            case LIME_DYE:
                return DyeColor.LIME;
            case MAGENTA_DYE:
                return DyeColor.MAGENTA;
            case ORANGE_DYE:
                return DyeColor.ORANGE;
            case PINK_DYE:
                return DyeColor.PINK;
            case PURPLE_DYE:
                return DyeColor.PURPLE;
            case RED_DYE:
                return DyeColor.RED;
            case WHITE_DYE:
                return DyeColor.WHITE;
            case YELLOW_DYE:
                return DyeColor.YELLOW;
        }
        return null;
    }
    
}
