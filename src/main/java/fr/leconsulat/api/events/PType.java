package fr.leconsulat.api.events;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum PType {
    
    DOOR(PCategory.DOOR_INTERACTION),
    BUTTON(PCategory.REDSTONE_INTERACTION),
    FENCE(PCategory.OTHER), //Leash
    FENCE_GATE(PCategory.DOOR_INTERACTION),
    SIGN(PCategory.COLOR), //Color
    NOTHING(PCategory.OTHER), //Stairs
    TRAPDOOR(PCategory.DOOR_INTERACTION),
    ANVIL(PCategory.OPEN_GUI),
    BARREL(PCategory.CONTAINER),
    BEACON(PCategory.CONTAINER),
    BELL(PCategory.OTHER),
    BED(PCategory.OTHER),
    SHULKER_BOX(PCategory.CONTAINER),
    BLAST_FURNACE(PCategory.CONTAINER),
    BREWING_STAND(PCategory.CONTAINER),
    CAKE(PCategory.OTHER),
    CAMPFIRE(PCategory.OTHER),
    CARTOGRAPHY_TABLE(PCategory.OPEN_GUI),
    CAULDRON(PCategory.OTHER),
    OPERATOR(PCategory.FORBIDDEN),
    CHEST(PCategory.CONTAINER),
    COMPARATOR(PCategory.CHANGE_REDSTONE),
    COMPOSTER(PCategory.OTHER),
    CRAFTING_TABLE(PCategory.OPEN_GUI),
    DAYLIGHT_DETECTOR(PCategory.CHANGE_REDSTONE),
    DISPENSER(PCategory.CONTAINER),
    DRAGON_EGG(PCategory.OTHER),
    DROPPER(PCategory.CONTAINER),
    ENCHANTING_TABLE(PCategory.OPEN_GUI),
    ENDER_CHEST(PCategory.OPEN_GUI),
    FLETCHING_TABLE(PCategory.OPEN_GUI),
    FLOWER_POT(PCategory.OTHER),
    FURNACE(PCategory.CONTAINER),
    GRINDSTONE(PCategory.OPEN_GUI),
    HOPPER(PCategory.CONTAINER),
    JUKEBOX(PCategory.CONTAINER),
    LECTERN(PCategory.CONTAINER),
    LEVER(PCategory.REDSTONE_INTERACTION),
    LOOM(PCategory.OPEN_GUI),
    NOTE_BLOCK(PCategory.OTHER),
    PUMPKIN(PCategory.OTHER),
    REDSTONE_ORE(PCategory.OTHER),
    REPEATER(PCategory.CHANGE_REDSTONE),
    SMITHING_TABLE(PCategory.OPEN_GUI),
    SMOKER(PCategory.CONTAINER),
    STONECUTTER(PCategory.OPEN_GUI),
    SWEET_BERRY_BUSH(PCategory.OTHER),
    TNT(PCategory.CHANGE_REDSTONE),
    PRESSURE_PLATE(PCategory.REDSTONE_INTERACTION),
    FARMLAND(PCategory.OTHER),
    TRIPWIRE(PCategory.REDSTONE_INTERACTION),
    TURTLE_EGG(PCategory.OTHER),
    NO_INTERACTION(PCategory.OTHER);
    
    private PCategory category;
    
    PType(PCategory category){
        this.category = category;
    }
    
    public static @NotNull PType getCategory(Material type){
        switch(type){
            case ACACIA_BUTTON:
            case BIRCH_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
                return BUTTON;
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case DARK_OAK_DOOR:
            case JUNGLE_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
                return DOOR;
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case NETHER_BRICK_FENCE:
            case OAK_FENCE:
            case SPRUCE_FENCE:
                return FENCE;
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                return FENCE_GATE;
            case ACACIA_SIGN:
            case ACACIA_WALL_SIGN:
            case BIRCH_SIGN:
            case BIRCH_WALL_SIGN:
            case DARK_OAK_SIGN:
            case DARK_OAK_WALL_SIGN:
            case JUNGLE_SIGN:
            case JUNGLE_WALL_SIGN:
            case OAK_SIGN:
            case OAK_WALL_SIGN:
            case SPRUCE_SIGN:
            case SPRUCE_WALL_SIGN:
                return SIGN;
            case ACACIA_STAIRS:
            case ANDESITE_STAIRS:
            case BIRCH_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case DARK_PRISMARINE_STAIRS:
            case DIORITE_STAIRS:
            case END_STONE_BRICK_STAIRS:
            case GRANITE_STAIRS:
            case IRON_DOOR:
            case IRON_TRAPDOOR:
            case JUNGLE_STAIRS:
            case MOSSY_COBBLESTONE_STAIRS:
            case MOSSY_STONE_BRICK_STAIRS:
            case MOVING_PISTON:
            case NETHER_BRICK_STAIRS:
            case OAK_STAIRS:
            case POLISHED_ANDESITE_STAIRS:
            case POLISHED_DIORITE_STAIRS:
            case POLISHED_GRANITE_STAIRS:
            case PRISMARINE_BRICK_STAIRS:
            case PRISMARINE_STAIRS:
            case PURPUR_STAIRS:
            case QUARTZ_STAIRS:
            case RED_NETHER_BRICK_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case SANDSTONE_STAIRS:
            case SMOOTH_QUARTZ_STAIRS:
            case SMOOTH_RED_SANDSTONE_STAIRS:
            case SMOOTH_SANDSTONE_STAIRS:
            case SPRUCE_STAIRS:
            case STONE_BRICK_STAIRS:
            case STONE_STAIRS:
                return NOTHING;
            case ACACIA_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
                return TRAPDOOR;
            case ANVIL:
            case CHIPPED_ANVIL:
            case DAMAGED_ANVIL:
                return ANVIL;
            case BARREL:
                return BARREL;
            case BEACON:
                return BEACON;
            case BELL:
                return BELL;
            case BLACK_BED:
            case BLUE_BED:
            case BROWN_BED:
            case CYAN_BED:
            case GRAY_BED:
            case GREEN_BED:
            case LIGHT_BLUE_BED:
            case LIGHT_GRAY_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case ORANGE_BED:
            case PINK_BED:
            case PURPLE_BED:
            case RED_BED:
            case WHITE_BED:
            case YELLOW_BED:
                return BED;
            case BLACK_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
                return SHULKER_BOX;
            case BLAST_FURNACE:
                return BLAST_FURNACE;
            case BREWING_STAND:
                return BREWING_STAND;
            case CAKE:
                return CAKE;
            case CAMPFIRE:
                return CAMPFIRE;
            case CARTOGRAPHY_TABLE:
                return CARTOGRAPHY_TABLE;
            case CAULDRON:
                return CAULDRON;
            case CHAIN_COMMAND_BLOCK:
            case COMMAND_BLOCK:
            case JIGSAW:
            case REPEATING_COMMAND_BLOCK:
            case STRUCTURE_BLOCK:
                return OPERATOR;
            case CHEST:
            case TRAPPED_CHEST:
                return CHEST;
            case COMPARATOR:
                return COMPARATOR;
            case COMPOSTER:
                return COMPOSTER;
            case CRAFTING_TABLE:
                return CRAFTING_TABLE;
            case DAYLIGHT_DETECTOR:
                return DAYLIGHT_DETECTOR;
            case DISPENSER:
                return DISPENSER;
            case DRAGON_EGG:
                return DRAGON_EGG;
            case DROPPER:
                return DROPPER;
            case ENCHANTING_TABLE:
                return ENCHANTING_TABLE;
            case ENDER_CHEST:
                return ENDER_CHEST;
            case FLETCHING_TABLE:
                return FLETCHING_TABLE;
            case FLOWER_POT:
            case POTTED_ACACIA_SAPLING:
            case POTTED_ALLIUM:
            case POTTED_AZURE_BLUET:
            case POTTED_BAMBOO:
            case POTTED_BIRCH_SAPLING:
            case POTTED_BLUE_ORCHID:
            case POTTED_BROWN_MUSHROOM:
            case POTTED_CACTUS:
            case POTTED_CORNFLOWER:
            case POTTED_DANDELION:
            case POTTED_DARK_OAK_SAPLING:
            case POTTED_DEAD_BUSH:
            case POTTED_FERN:
            case POTTED_JUNGLE_SAPLING:
            case POTTED_LILY_OF_THE_VALLEY:
            case POTTED_OAK_SAPLING:
            case POTTED_ORANGE_TULIP:
            case POTTED_OXEYE_DAISY:
            case POTTED_PINK_TULIP:
            case POTTED_POPPY:
            case POTTED_RED_MUSHROOM:
            case POTTED_RED_TULIP:
            case POTTED_SPRUCE_SAPLING:
            case POTTED_WHITE_TULIP:
            case POTTED_WITHER_ROSE:
                return FLOWER_POT;
            case FURNACE:
                return FURNACE;
            case GRINDSTONE:
                return GRINDSTONE;
            case HOPPER:
                return HOPPER;
            case JUKEBOX:
                return JUKEBOX;
            case LECTERN:
                return LECTERN;
            case LEVER:
                return LEVER;
            case LOOM:
                return LOOM;
            case NOTE_BLOCK:
                return NOTE_BLOCK;
            case PUMPKIN:
                return PUMPKIN;
            case REDSTONE_ORE:
                return REDSTONE_ORE;
            case REPEATER:
                return REPEATER;
            case SMITHING_TABLE:
                return SMITHING_TABLE;
            case SMOKER:
                return SMOKER;
            case STONECUTTER:
                return STONECUTTER;
            case SWEET_BERRY_BUSH:
                return SWEET_BERRY_BUSH;
            case TNT:
                return TNT;
            case ACACIA_PRESSURE_PLATE:
            case OAK_PRESSURE_PLATE:
            case BIRCH_PRESSURE_PLATE:
            case STONE_PRESSURE_PLATE:
            case JUNGLE_PRESSURE_PLATE:
            case SPRUCE_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE:
            case HEAVY_WEIGHTED_PRESSURE_PLATE:
            case LIGHT_WEIGHTED_PRESSURE_PLATE:
                return PRESSURE_PLATE;
            case FARMLAND:
                return FARMLAND;
            case TRIPWIRE:
                return TRIPWIRE;
            case TURTLE_EGG:
                return TURTLE_EGG;
        }
        return NO_INTERACTION;
    }
    
}
