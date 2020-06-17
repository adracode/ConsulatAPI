package fr.leconsulat.api.gui;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.gui.events.GuiCloseEvent;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;

public interface IGui<T> extends InventoryHolder {
    
    GuiListener<T> getListener();
    
    PagedGui<T> getPagedGui();
    
    Gui<?> getFather();
    
    default void onClose(ConsulatPlayer player){
        final GuiCloseEvent<T> event = new GuiCloseEvent<T>(player, getPagedGui(), getPagedGui().getGui().getData(), getFather(), true);
        getListener().onClose(event);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () -> {
            if(event.isCancelled()){
                getPagedGui().open(event.getPlayer());
                return;
            }
            if(player.getPlayer().getOpenInventory().getTitle().equals("Crafting")){
                player.setCurrentlyOpen(null);
                if(event.getFather() != null && event.openFatherGui()){
                    event.getFather().open(player);
                }
            }
        }, 1L);
    }
}
