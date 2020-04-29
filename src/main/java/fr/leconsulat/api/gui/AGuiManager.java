package fr.leconsulat.api.gui;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.events.AGuiInteractEvent;
import fr.leconsulat.api.events.ConsulatPlayerLeaveEvent;
import fr.leconsulat.api.events.ConsulatPlayerLoadedEvent;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AGuiManager implements Listener {
    
    private static AGuiManager instance;
    
    //Store all root
    private final Map<String, AGListener> roots = new HashMap<>();
    private List<AGListener> playerGuis = new ArrayList<>();
    
    public AGuiManager(ConsulatAPI core){
        if(instance != null){
            return;
        }
        instance = this;
        core.getServer().getPluginManager().registerEvents(this, core);
    }
    
    public static AGuiManager getInstance(){
        return instance;
    }
    
    /**
     * Ajouter un gui principal
     * @param name l'identifiant du gui
     * @param gui le listener
     */
    public void addRootGui(String name, AGListener gui){
        roots.put(name, gui);
    }
    
    /**
     * Renvoie un gui principale
     * @param name l'identifiant du gui
     * @return le listener
     */
    public AGListener getRootGui(String name){
        return roots.get(name);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClickInventory(InventoryClickEvent e){
        ItemStack item = e.getCurrentItem();
        if(item == null){
            return;
        }
        AGListener gui = getListener(e.getWhoClicked().getOpenInventory().getTopInventory());
        if(gui != null && !gui.isModifiable()){
            e.setCancelled(true);
        }
        gui = getListener(e.getClickedInventory());
        if(gui == null){
            return;
        }
        if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS) &&
                item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) &&
                item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)){
            e.setCancelled(true);
            if(item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON)){
                return;
            }
            ConsulatAPI.getConsulatAPI().getServer().getPluginManager().callEvent(new AGuiInteractEvent(gui, (byte)e.getSlot(), e.getClick(), CPlayerManager.getInstance().getConsulatPlayer(e.getWhoClicked().getUniqueId())));
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteractGui(AGuiInteractEvent e){
        e.getGui().onClick(new AGClickEvent(e.getPlayer().getCurrentlyOpen(), e.getSlot(), e.getClick(), e.getPlayer()));
    }
    
    private AGListener getListener(Inventory inventory){
        if(inventory == null){
            return null;
        }
        InventoryHolder holder = inventory.getHolder();
        if(!(holder instanceof AGui)){
            return null;
        }
        return ((AGui)holder).getListener();
    }
    
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent e){
        AGListener listener = getListener(e.getInventory());
        if(listener != null){
            final ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(e.getPlayer().getUniqueId());
            listener.close(new AGCloseEvent(player, true));
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(ConsulatPlayerLoadedEvent e){
        for(AGListener listener : playerGuis){
            listener.create(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onQuit(ConsulatPlayerLeaveEvent e){
        for(AGListener listener : playerGuis){
            listener.removeGui(e.getPlayer());
        }
    }
    
    /**
     * Indique que le listener a une clé de type ConsulatPlayer
     * @param listener
     */
    public void setPlayerBounded(AGListener listener){
        this.playerGuis.add(listener);
    }
}