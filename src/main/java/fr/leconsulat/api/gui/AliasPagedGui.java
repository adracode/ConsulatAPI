package fr.leconsulat.api.gui;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class AliasPagedGui<T> implements IGui {
    
    /* Champs utilisés pour modifier le titre de l'inventaire */
    private static Field inventoryField;
    private static Field titleField;
    private static Field inventoryContent;
    
    /* Initialise les champs ci dessus */
    static{
        try {
            inventoryField = MinecraftReflection.getCraftBukkitClass("inventory.CraftInventory").getDeclaredField("inventory");
            inventoryField.setAccessible(true);
            titleField = MinecraftReflection.getCraftBukkitClass("inventory.CraftInventoryCustom$MinecraftInventory").getDeclaredField("title");
            titleField.setAccessible(true);
            inventoryContent = MinecraftReflection.getCraftBukkitClass("inventory.CraftInventoryCustom$MinecraftInventory").getDeclaredField("items");
            inventoryContent.setAccessible(true);
        } catch(NoSuchFieldException e){
            e.printStackTrace();
        }
    }
    
    private PagedGui<T> pointedGui;
    private Inventory inventory;
    private Gui<?> father;
    
    public AliasPagedGui(PagedGui<T> pointedGui, Gui<?> father, String name){
        this.pointedGui = pointedGui;
        this.father = father;
        Inventory pointedInventory = pointedGui.getInventory();
        this.inventory = Bukkit.createInventory(this, pointedInventory.getSize(), father.getName() + " > " + name);
        try {
            inventoryContent.set(inventoryField.get(this.inventory), inventoryContent.get(inventoryField.get(pointedInventory)));
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }
    
    public void open(ConsulatPlayer player){
        player.getPlayer().openInventory(inventory);
    }
    
    @Override
    public @NotNull Inventory getInventory(){
        return inventory;
    }
    
    @Override
    public GuiListener<T> getListener(){
        return pointedGui.getListener();
    }
    
    @Override
    public PagedGui<T> getPagedGui(){
        return pointedGui;
    }
    
    @Override
    public Gui<?> getFather(){
        return father;
    }
}
