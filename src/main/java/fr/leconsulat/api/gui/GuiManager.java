package fr.leconsulat.api.gui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.gui.events.GuiClickEvent;
import fr.leconsulat.api.gui.input.UserInput;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.utils.minecraft.packets.PacketUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiManager implements Listener {
    
    private static GuiManager instance;
    
    //Store all root
    private final Map<String, GuiListener<?>> roots = new HashMap<>();
    private final Map<UUID, UserInput> inputs = new HashMap<>();
    
    public GuiManager(ConsulatAPI core){
        if(instance != null){
            return;
        }
        instance = this;
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ConsulatAPI.getConsulatAPI(), PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event){
                Player player = event.getPlayer();
                UserInput userInput = inputs.remove(player.getUniqueId());
                if(userInput == null){
                    return;
                }
                event.setCancelled(true);
                userInput.processInput(PacketUtils.getArrayFromUpdateSignPacket(event.getPacket().getHandle()));
            }
        });
        core.getServer().getPluginManager().registerEvents(this, core);
    }
    
    public static GuiManager getInstance(){
        return instance;
    }
    
    public void userInput(Player player, Consumer<String> consumer, String[] defaultText, int... result){
        UserInput userInput = new UserInput(consumer, defaultText, result);
        inputs.put(player.getUniqueId(), userInput);
        userInput.open(player);
    }
    
    /**
     * Ajouter un gui principal
     *
     * @param name l'identifiant du gui
     * @param gui  le listener
     */
    public void addRootGui(String name, GuiListener<?> gui){
        roots.put(name, gui);
    }
    
    /**
     * Renvoie un gui principale
     *
     * @param name l'identifiant du gui
     * @return le listener
     */
    public GuiListener<?> getRootGui(String name){
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
        GuiListener<?> listener = getListener(e.getWhoClicked().getOpenInventory().getTopInventory());
        if(listener == null){
            return;
        }
        if(!listener.isModifiable()){
            e.setCancelled(true);
        }
        IGui<?> gui = getGui(e.getClick() == ClickType.NUMBER_KEY ? e.getWhoClicked().getInventory() : e.getClickedInventory());
        if(gui == null){
            return;
        }
        e.setCancelled(true);
        if(e.getClick() == ClickType.DOUBLE_CLICK){
            return;
        }
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(e.getWhoClicked().getUniqueId());
        listener.onClick(new GuiClickEvent(gui.getPagedGui(), e.getSlot(), e.getClick(), player));
    }
    
    private IGui<?> getGui(Inventory inventory){
        if(inventory == null){
            return null;
        }
        InventoryHolder holder = inventory.getHolder();
        if(!(holder instanceof IGui)){
            return null;
        }
        return ((IGui<?>)holder).getPagedGui();
    }
    
    private GuiListener<?> getListener(Inventory inventory){
        if(inventory == null){
            return null;
        }
        InventoryHolder holder = inventory.getHolder();
        if(!(holder instanceof IGui)){
            return null;
        }
        return ((IGui<?>)holder).getListener();
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onClose(InventoryCloseEvent e){
        if(!(e.getInventory().getHolder() instanceof IGui)){
            return;
        }
        IGui<?> gui = (IGui<?>)e.getInventory().getHolder();
        gui.onClose(CPlayerManager.getInstance().getConsulatPlayer(e.getPlayer().getUniqueId()));
    }
    
    void removeInput(){
    
    }
    
}