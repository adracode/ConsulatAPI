package fr.leconsulat.api.events;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.events.blocks.*;
import fr.leconsulat.api.events.entities.*;
import fr.leconsulat.api.events.items.*;
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
    
    static{
        new EventManager();
    }
    
    private EventManager(){
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
                PlayerClickBlockEvent clickBlockEvent = new PlayerClickBlockEvent(clickedBlock, player, event.getHand());
                Bukkit.getPluginManager().callEvent(clickBlockEvent);
                if(clickBlockEvent.isCancelled()){
                    event.setCancelled(true);
                    return;
                }
                boolean interact = !noInteraction(player);
                if(interact){
                    PType type = PType.getCategory(clickedBlock.getType());
                    PlayerInteractBlockEvent interactBlockEvent;
                    switch(type){
                        case BUTTON:
                            interactBlockEvent = new PlayerInteractButtonEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case DOOR:
                            interactBlockEvent = new PlayerInteractDoorEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case FENCE:
                            interactBlockEvent = new PlayerInteractFenceEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case FENCE_GATE:
                            interactBlockEvent = new PlayerInteractFenceGateEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case SIGN:
                            interactBlockEvent = new PlayerInteractSignEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case TRAPDOOR:
                            interactBlockEvent = new PlayerInteractTrapdoorEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case ANVIL:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.ANVIL);
                            break;
                        case BEACON:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.BEACON);
                            break;
                        case CARTOGRAPHY_TABLE:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.CARTOGRAPHY_TABLE);
                            break;
                        case CRAFTING_TABLE:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.CRAFTING_TABLE);
                            break;
                        case ENCHANTING_TABLE:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.ENCHANTING_TABLE);
                            break;
                        case ENDER_CHEST:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.ENDER_CHEST);
                            break;
                        case FLETCHING_TABLE:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.FLETCHING_TABLE);
                            break;
                        case GRINDSTONE:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.GRINDSTONE);
                            break;
                        case LOOM:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.LOOM);
                            break;
                        case SMITHING_TABLE:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.SMITHING_TABLE);
                            break;
                        case STONECUTTER:
                            interactBlockEvent = new PlayerInteractGuiBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractGuiBlockEvent.Type.STONECUTTER);
                            break;
                        case BARREL:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.BARREL);
                            break;
                        case BLAST_FURNACE:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.BLAST_FURNACE);
                            break;
                        case BREWING_STAND:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.BREWING_STAND);
                            break;
                        case CHEST:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.CHEST);
                            break;
                        case DISPENSER:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.DISPENSER);
                            break;
                        case DROPPER:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.DROPPER);
                            break;
                        case FURNACE:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.FURNACE);
                            break;
                        case HOPPER:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.HOPPER);
                            break;
                        case SHULKER_BOX:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.SHULKER_BOX);
                            break;
                        case SMOKER:
                            interactBlockEvent = new PlayerInteractContainerBlockEvent(event.getClickedBlock(), player, event.getHand(), PlayerInteractContainerBlockEvent.Type.SMOKER);
                            break;
                        case BELL:
                            interactBlockEvent = new PlayerInteractBellEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case BED:
                            interactBlockEvent = new PlayerInteractBedEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case CAKE:
                            interactBlockEvent = new PlayerInteractCakeEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case CAMPFIRE:
                            interactBlockEvent = new PlayerInteractCampfireEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case CAULDRON:
                            interactBlockEvent = new PlayerInteractCauldronEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case OPERATOR:
                            interactBlockEvent = new PlayerInteractForbiddenEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case COMPARATOR:
                        case DAYLIGHT_DETECTOR:
                        case REPEATER:
                            interactBlockEvent = new PlayerInteractRedstoneComponentEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case COMPOSTER:
                            interactBlockEvent = new PlayerInteractComposterEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case DRAGON_EGG:
                            interactBlockEvent = new PlayerInteractDragonEggEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case FLOWER_POT:
                            interactBlockEvent = new PlayerInteractFlowerPotEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case JUKEBOX:
                            interactBlockEvent = new PlayerInteractJukeboxEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case LECTERN:
                            interactBlockEvent = new PlayerInteractLecternEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case LEVER:
                            interactBlockEvent = new PlayerInteractLeverEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case NOTE_BLOCK:
                            interactBlockEvent = new PlayerInteractNoteEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case PUMPKIN:
                            interactBlockEvent = new PlayerInteractPumpkinEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case REDSTONE_ORE:
                            interactBlockEvent = new PlayerInteractRedstoneOreEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case SWEET_BERRY_BUSH:
                            interactBlockEvent = new PlayerInteractBerryEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        case TNT:
                            interactBlockEvent = new PlayerInteractTntEvent(event.getClickedBlock(), player, event.getHand());
                            break;
                        default:
                            onItemUse(event, player, clickedBlock);
                            return;
                    }
                    Bukkit.getPluginManager().callEvent(interactBlockEvent);
                    if(interactBlockEvent.isCancelled()){
                        event.setCancelled(true);
                    }
                } else {
                    onItemUse(event, player, clickedBlock);
                }
            }
            break;
            case PHYSICAL:
                if(clickedBlock == null){
                    return;
                }
                PType type = PType.getCategory(clickedBlock.getType());
                PlayerInteractBlockEvent interactBlockEvent = null;
                switch(type){
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
                }
                if(interactBlockEvent != null){
                    Bukkit.getPluginManager().callEvent(interactBlockEvent);
                    if(interactBlockEvent.isCancelled()){
                        event.setCancelled(true);
                    }
                }
                break;
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent interactEvent){
        Entity entity = interactEvent.getRightClicked();
        Player player = interactEvent.getPlayer();
        ItemStack itemInHand = player.getInventory().getItem(interactEvent.getHand());
        Material material = itemInHand == null ? Material.AIR : itemInHand.getType();
        Event event = null;
        switch(material){
            case NAME_TAG:
                if(entity.getType() != EntityType.PLAYER && itemInHand.getItemMeta().hasDisplayName()){
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
            default:
                if(isSpawnEgg(material)){
                    event = new PlayerSpawnEntityEvent(player, entity.getLocation(), itemInHand);
                }
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
                    switch(material){
                        case BUCKET:
                        case WATER_BUCKET:
                            event = new PlayerPickupFishEvent(entity, player);
                            break;
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
                //TODO: test
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
                case MINECART_COMMAND:
                    event = new PlayerOpenCommandBlockEvent(entity, player);
                    break;
                case VILLAGER:
                    Villager villager = (Villager)entity;
                    if(!villager.isTrading() && villager.getRecipeCount() != 0 && !villager.isSleeping() && villager.isAdult()){
                        event = new PlayerOpenVillagerEvent(villager, player);
                    }
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
                case LEASH_HITCH://Handled by EntityLeashEvent
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
    
    private boolean noInteraction(Player player){
        return player.isSneaking() && (player.getInventory().getItemInMainHand().getType() != Material.AIR || player.getInventory().getItemInOffHand().getType() != Material.AIR);
    }
    
    private void onItemUse(PlayerInteractEvent event, Player player, Block clickedBlock){
        if(event.getItem() != null){
            PlayerPlaceItemEvent playerPlaceItemEvent = null;
            switch(event.getItem().getType()){
                case ARMOR_STAND:
                    playerPlaceItemEvent = new PlayerPlaceArmorStandEvent(player, clickedBlock.getLocation(), event.getItem());
                    break;
                case BIRCH_BOAT:
                case ACACIA_BOAT:
                case DARK_OAK_BOAT:
                case JUNGLE_BOAT:
                case OAK_BOAT:
                case SPRUCE_BOAT:
                    playerPlaceItemEvent = new PlayerPlaceBoatEvent(player, clickedBlock.getLocation(), event.getItem());
                    break;
                case END_CRYSTAL:
                    playerPlaceItemEvent = new PlayerPlaceEndCrystalEvent(player, clickedBlock.getLocation(), event.getItem());
                    break;
                case MINECART:
                case CHEST_MINECART:
                case FURNACE_MINECART:
                case TNT_MINECART:
                case HOPPER_MINECART:
                case COMMAND_BLOCK_MINECART:
                    playerPlaceItemEvent = new PlayerPlaceMinecartEvent(player, clickedBlock.getLocation(), event.getItem());
                    break;
                default:
                    if(isSpawnEgg(event.getMaterial())){
                        playerPlaceItemEvent = new PlayerSpawnEntityEvent(player, clickedBlock.getLocation(), event.getItem());
                    }
            }
            if(playerPlaceItemEvent != null){
                Bukkit.getPluginManager().callEvent(playerPlaceItemEvent);
                if(playerPlaceItemEvent.isCancelled()){
                    event.setCancelled(true);
                }
            }
        }
    }
    
    private boolean isSpawnEgg(Material material){
        switch(material){
            case BAT_SPAWN_EGG:
            case BLAZE_SPAWN_EGG:
            case CAT_SPAWN_EGG:
            case CAVE_SPIDER_SPAWN_EGG:
            case CHICKEN_SPAWN_EGG:
            case COD_SPAWN_EGG:
            case COW_SPAWN_EGG:
            case CREEPER_SPAWN_EGG:
            case DOLPHIN_SPAWN_EGG:
            case DONKEY_SPAWN_EGG:
            case DROWNED_SPAWN_EGG:
            case ELDER_GUARDIAN_SPAWN_EGG:
            case ENDERMAN_SPAWN_EGG:
            case ENDERMITE_SPAWN_EGG:
            case EVOKER_SPAWN_EGG:
            case FOX_SPAWN_EGG:
            case GHAST_SPAWN_EGG:
            case GUARDIAN_SPAWN_EGG:
            case HORSE_SPAWN_EGG:
            case HUSK_SPAWN_EGG:
            case LLAMA_SPAWN_EGG:
            case MAGMA_CUBE_SPAWN_EGG:
            case MOOSHROOM_SPAWN_EGG:
            case MULE_SPAWN_EGG:
            case OCELOT_SPAWN_EGG:
            case PANDA_SPAWN_EGG:
            case PARROT_SPAWN_EGG:
            case PHANTOM_SPAWN_EGG:
            case PIG_SPAWN_EGG:
            case PILLAGER_SPAWN_EGG:
            case POLAR_BEAR_SPAWN_EGG:
            case PUFFERFISH_SPAWN_EGG:
            case RABBIT_SPAWN_EGG:
            case RAVAGER_SPAWN_EGG:
            case SALMON_SPAWN_EGG:
            case SHEEP_SPAWN_EGG:
            case SHULKER_SPAWN_EGG:
            case SILVERFISH_SPAWN_EGG:
            case SKELETON_HORSE_SPAWN_EGG:
            case SKELETON_SPAWN_EGG:
            case SLIME_SPAWN_EGG:
            case SPIDER_SPAWN_EGG:
            case SQUID_SPAWN_EGG:
            case STRAY_SPAWN_EGG:
            case TRADER_LLAMA_SPAWN_EGG:
            case TROPICAL_FISH_SPAWN_EGG:
            case TURTLE_SPAWN_EGG:
            case VEX_SPAWN_EGG:
            case VILLAGER_SPAWN_EGG:
            case VINDICATOR_SPAWN_EGG:
            case WANDERING_TRADER_SPAWN_EGG:
            case WITCH_SPAWN_EGG:
            case WITHER_SKELETON_SPAWN_EGG:
            case WOLF_SPAWN_EGG:
            case ZOMBIE_HORSE_SPAWN_EGG:
            case ZOMBIE_PIGMAN_SPAWN_EGG:
            case ZOMBIE_SPAWN_EGG:
            case ZOMBIE_VILLAGER_SPAWN_EGG:
                return true;
        }
        return false;
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
    
    public static EventManager getInstance(){
        return instance;
    }
}
