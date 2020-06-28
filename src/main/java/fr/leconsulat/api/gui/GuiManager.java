package fr.leconsulat.api.gui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.gui.gui.module.api.Relationnable;
import fr.leconsulat.api.gui.input.UserInput;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.utils.minecraft.packets.PacketUtils;
import org.bukkit.Bukkit;
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
    
    private final Map<UUID, UserInput> inputs = new HashMap<>();
    private final Map<String, GuiContainer<?>> containers = new HashMap<>();
    
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
        IGui gui = getGui(e.getWhoClicked().getOpenInventory().getTopInventory());
        boolean isGuiItem = GuiItem.isGuiItem(item);
        if(gui == null){
            if(isGuiItem){
                e.setCancelled(true);
            }
            return;
        }
        if(!gui.isModifiable()){
            e.setCancelled(true);
        }
        IGui clickedInventory = getGui(e.getClick() == ClickType.NUMBER_KEY ? e.getWhoClicked().getInventory() : e.getClickedInventory());
        if(clickedInventory == null){
            if(isGuiItem){
                e.setCancelled(true);
            }
            return;
        }
        e.setCancelled(true);
        if(e.getClick() == ClickType.DOUBLE_CLICK){
            return;
        }
        GuiItem clickedItem = clickedInventory.getItem(e.getSlot());
        if(clickedItem == null){
            return;
        }
        if(clickedItem.getDisplayName().equals(" ")){
            return;
        }
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(e.getWhoClicked().getUniqueId());
        if(clickedItem.getSlot() == (clickedInventory.getLine() - 1) * 9 &&
                clickedItem.getDisplayName().equals("§cRetour") &&
                clickedInventory instanceof Relationnable &&
                ((Relationnable)clickedInventory).hasFather()){
            ((Relationnable)clickedInventory).getFather().open(player);
            return;
        }
        clickedInventory.onClick(new GuiClickEvent(e.getSlot(), e.getClick(), player));
    }
    
    private IGui getGui(Inventory inventory){
        if(inventory == null){
            return null;
        }
        InventoryHolder holder = inventory.getHolder();
        if(!(holder instanceof IGui)){
            return null;
        }
        return ((IGui)holder);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onClose(InventoryCloseEvent e){
        IGui gui = getGui(e.getInventory());
        if(gui == null){
            return;
        }
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(e.getPlayer().getUniqueId());
        GuiCloseEvent event = new GuiCloseEvent(player);
        gui.onClose(event);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () -> {
            if(event.isCancelled()){
                gui.open(event.getPlayer());
                return;
            }
            if(player.getPlayer().getOpenInventory().getTitle().equals("Crafting")){
                player.setCurrentlyOpen(null);
                if(event.openFatherGui()){
                    if(gui instanceof Relationnable && ((Relationnable)gui).hasFather()){
                        ((Relationnable)gui).getFather().open(player);
                    } else if(gui instanceof Pageable){
                        Pageable page = ((Pageable)gui);
                        if(page.getMainPage().getBaseGui() instanceof Relationnable){
                            Relationnable relationnableGui = (Relationnable)page.getMainPage().getBaseGui();
                            if(relationnableGui.hasFather()){
                                relationnableGui.getFather().open(player);
                            }
                        }
                    }
                }
            }
        }, 1L);
    }
    
    public void addContainer(String id, GuiContainer<?> container){
        this.containers.put(id, container);
    }
    
    public void removeContainer(String id){
        this.containers.remove(id);
    }
    
    @SuppressWarnings("unchecked")
    public <A> GuiContainer<A> getContainer(String id){
        return (GuiContainer<A>)this.containers.get(id);
    }
    
}