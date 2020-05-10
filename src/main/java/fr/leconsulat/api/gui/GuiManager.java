package fr.leconsulat.api.gui;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.events.ConsulatPlayerLeaveEvent;
import fr.leconsulat.api.events.ConsulatPlayerLoadedEvent;
import fr.leconsulat.api.gui.events.GuiClickEvent;
import fr.leconsulat.api.gui.events.GuiCloseEvent;
import fr.leconsulat.api.gui.events.GuiInteractEvent;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiManager implements Listener {
    
    private static GuiManager instance;
    
    //Store all root
    private final Map<String, GuiListener> roots = new HashMap<>();
    private List<GuiListener> playerGuis = new ArrayList<>();
    
    public GuiManager(ConsulatAPI core){
        if(instance != null){
            return;
        }
        instance = this;
        core.getServer().getPluginManager().registerEvents(this, core);
    }
    
    public static GuiManager getInstance(){
        return instance;
    }
    
    /**
     * Ajouter un gui principal
     *
     * @param name l'identifiant du gui
     * @param gui  le listener
     */
    public void addRootGui(String name, GuiListener gui){
        roots.put(name, gui);
    }
    
    /**
     * Renvoie un gui principale
     *
     * @param name l'identifiant du gui
     * @return le listener
     */
    public GuiListener getRootGui(String name){
        return roots.get(name);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onClickInventory(InventoryClickEvent e){
        ItemStack item = e.getCurrentItem();
        if(e.getClick() == ClickType.NUMBER_KEY && item == null){
            if(e.getWhoClicked().getInventory().getItem(e.getHotbarButton()) == null){
                return;
            }
            item = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
        }
        if(item == null){
            return;
        }
        GuiListener gui = getListener(e.getWhoClicked().getOpenInventory().getTopInventory());
        if(gui != null && !gui.isModifiable()){
            e.setCancelled(true);
        }
        gui = getListener(e.getClick() == ClickType.NUMBER_KEY ? e.getWhoClicked().getInventory() : e.getClickedInventory());
        if(gui == null){
            return;
        }
        e.setCancelled(true);
        ConsulatAPI.getConsulatAPI().getServer().getPluginManager().callEvent(new GuiInteractEvent(gui, (byte)e.getSlot(), e.getClick(), CPlayerManager.getInstance().getConsulatPlayer(e.getWhoClicked().getUniqueId())));
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteractGui(GuiInteractEvent e){
        e.getGui().onClick(new GuiClickEvent(e.getPlayer().getCurrentlyOpen(), e.getSlot(), e.getClick(), e.getPlayer()));
    }
    
    private GuiListener getListener(Inventory inventory){
        if(inventory == null){
            return null;
        }
        InventoryHolder holder = inventory.getHolder();
        if(!(holder instanceof Gui)){
            return null;
        }
        return ((Gui)holder).getListener();
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onClose(InventoryCloseEvent e){
        if(!(e.getInventory().getHolder() instanceof Gui)){
            return;
        }
        Gui gui = (Gui)e.getInventory().getHolder();
        GuiListener listener = gui.getListener();
        if(listener != null){
            final ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(e.getPlayer().getUniqueId());
            listener.close(new GuiCloseEvent(player, gui, gui.getKey(), true));
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(ConsulatPlayerLoadedEvent e){
        for(GuiListener listener : playerGuis){
            listener.create(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onQuit(ConsulatPlayerLeaveEvent e){
        for(GuiListener listener : playerGuis){
            listener.removeGui(e.getPlayer());
        }
    }
    
    /**
     * Indique que le listener a une clé de type ConsulatPlayer
     *
     * @param listener
     */
    public void setPlayerBounded(GuiListener listener){
        this.playerGuis.add(listener);
    }
}