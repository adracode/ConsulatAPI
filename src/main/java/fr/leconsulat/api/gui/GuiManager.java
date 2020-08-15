package fr.leconsulat.api.gui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiManager implements Listener {
    
    private static GuiManager instance;
    
    static {
        new GuiManager();
    }
    
    private final @NotNull Map<UUID, UserInput> inputs = new HashMap<>();
    private final @NotNull Map<String, GuiContainer<?>> containers = new HashMap<>();
    
    private GuiManager(){
        if(instance != null){
            throw new IllegalStateException();
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
                userInput.processInput(event.getPacket().getStringArrays().read(0));
            }
        });
        ConsulatAPI.getConsulatAPI().getProtocolManager().addPacketListener(new PacketAdapter(ConsulatAPI.getConsulatAPI(), PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event){
                Inventory top = event.getPlayer().getOpenInventory().getTopInventory();
                IGui gui = getGui(top);
                if(gui == null){
                    return;
                }
                if(!gui.containsFakeItems()){
                    return;
                }
                PacketContainer container = event.getPacket();
                List<ItemStack> items = container.getItemListModifier().read(0);
                for(GuiItem item : gui.getItems()){
                    if(item == null){
                        continue;
                    }
                    ItemStack fake = item.getFakeItem(event.getPlayer().getUniqueId());
                    if(fake == null){
                        continue;
                    }
                    items.set(item.getSlot(), fake);
                }
                container.getItemListModifier().write(0, items);
            }
        });
        Bukkit.getPluginManager().registerEvents(this, ConsulatAPI.getConsulatAPI());
    }
    
    public static GuiManager getInstance(){
        return instance;
    }
    
    public void userInput(ConsulatPlayer player, Consumer<String> consumer, String[] defaultText, int... result){
        UserInput userInput = new UserInput(consumer, defaultText, result);
        inputs.put(player.getUUID(), userInput);
        player.setCurrentlyOpen(null);
        player.getPlayer().closeInventory();
        userInput.open(player.getPlayer());
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
        if(clickedItem.getSlot() == (clickedInventory.getLine() - 1) * 9 && clickedItem.equals(GuiItem.BACK)){
            if(clickedInventory instanceof Pageable){
                clickedInventory = ((Pageable)clickedInventory).getMainPage().getGui();
            }
            if(clickedInventory instanceof Relationnable){
                Relationnable relationnable = (Relationnable)clickedInventory;
                if(relationnable.hasFather()){
                    relationnable.getFather().getGui().open(player);
                    return;
                }
            }
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
        if(!gui.equals(player.getCurrentlyOpen())){
            return;
        }
        GuiCloseEvent event = new GuiCloseEvent(player);
        gui.onClose(event);
        for(GuiItem item : gui.getItems()){
            if(item != null){
                item.clearFakeItems(player.getUUID());
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () -> {
            if(event.isCancelled()){
                gui.open(event.getPlayer());
                return;
            }
            if(player.getPlayer().getOpenInventory().getTitle().equals("Crafting")){
                player.setCurrentlyOpen(null);
                if(event.openFatherGui()){
                    if(gui instanceof Relationnable && ((Relationnable)gui).hasFather()){
                        ((Relationnable)gui).getFather().getGui().open(player);
                    } else if(gui instanceof Pageable){
                        Pageable page = ((Pageable)gui);
                        if(page.getMainPage().getGui() instanceof Relationnable){
                            Relationnable relationnableGui = (Relationnable)page.getMainPage().getGui();
                            if(relationnableGui.hasFather()){
                                relationnableGui.getFather().getGui().open(player);
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