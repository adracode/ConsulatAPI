package fr.leconsulat.api.gui.gui;

import fr.leconsulat.api.gui.GuiHeadItem;
import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.event.GuiRemoveEvent;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface IGui extends InventoryHolder {
    
    default void refresh(ConsulatPlayer player){
        if(player == null){
            return;
        }
        onOpened(new GuiOpenEvent(player));
    }
    
    default void refresh(){
        for(HumanEntity entity : getInventory().getViewers()){
            refresh(CPlayerManager.getInstance().getConsulatPlayer(entity.getUniqueId()));
        }
    }
    
    default void closeAll(){
        for(HumanEntity entity : getInventory().getViewers()){
            entity.closeInventory();
        }
    }
    
    default void onCreate(){
    }
    
    default void onOpen(GuiOpenEvent event){
    }
    
    default void onOpened(GuiOpenEvent event){
    }
    
    default void onClose(GuiCloseEvent event){
    }
    
    default void onClick(GuiClickEvent event){
    }
    
    default void onRemove(GuiRemoveEvent event){
    }
    
    default IGui removeFakeItem(int slot, ConsulatPlayer player){
        return setFakeItem(slot, null, player);
    }
    
    default void setDescriptionPlayer(int slot, ConsulatPlayer player, String... description){
        setDescriptionPlayer(slot, player, Arrays.asList(description));
    }
    
    default void setDescriptionPlayer(int slot, ConsulatPlayer player, List<String> description){
        GuiItem copy = getItem(slot);
        if(copy == null){
            return;
        }
        GuiItem fake = copy.clone();
        fake.setDescription(description);
        setFakeItem(slot, fake, player);
    }
    
    void update(int slot);
    
    void removeItems();
    
    IGui getBaseGui();
    
    @NotNull IGui setDeco(@NotNull Material type, int... slots);
    
    void setDisplayName(int slot, @NotNull String name);
    
    void setDescription(int slot, @NotNull String... description);
    
    void setType(int slot, @NotNull Material material);
    
    void setGlowing(int slot, boolean glow);
    
    @NotNull IGui setItem(@NotNull GuiItem item);
    
    @NotNull IGui setItem(int slot, @Nullable GuiItem item);
    
    void moveItem(int from, int to);
    
    void moveItem(int from, @NotNull IGui guiTo, int to);
    
    @Nullable GuiItem getItem(int slot);
    
    void open(@NotNull ConsulatPlayer player);
    
    @NotNull String getName();
    
    void setName(String name);
    
    String buildInventoryTitle(String title);
    
    void setTitle();
    
    void removeItem(int slot);
    
    @NotNull List<GuiItem> getItems();
    
    boolean isModifiable();
    
    void setModifiable(boolean modifiable);
    
    boolean isDestroyOnClose();
    
    void setDestroyOnClose(boolean destroyOnClose);
    
    boolean isBackButton();
    
    void setBackButton(boolean backButton);
    
    boolean containsFakeItems();
    
    IGui setFakeItem(int slot, ItemStack item, ConsulatPlayer player);
    
    default int getLine(){
        return getInventory().getSize() / 9;
    }
    
    /**
     * Créer et renvoie un nouvel item
     *
     * @param name        Le nom à afficher
     * @param slot        Le slot où l'item sera placé
     * @param material    Le type de l'item
     * @param description La description à afficher
     * @return L'item nouvellement crée
     */
    static GuiItem getItem(String name, int slot, Material material, String... description){
        return new GuiItem(name, (byte)slot, material, Arrays.asList(description));
    }
    
    static GuiHeadItem getItem(IGui gui, String name, int slot, String player, String... description){
        GuiHeadItem headItem = new GuiHeadItem(player, gui);
        headItem.setDisplayName(name).setSlot(slot).setDescription(description);
        return headItem;
    }
    
    static GuiHeadItem getItem(IGui gui, String name, int slot, UUID player, String... description){
        GuiHeadItem headItem = new GuiHeadItem(player, gui);
        headItem.setDisplayName(name).setSlot(slot).setDescription(description);
        return headItem;
    }
    
    static GuiItem getItem(GuiItem item, int slot){
        return item.clone().setSlot(slot);
    }
    
}
