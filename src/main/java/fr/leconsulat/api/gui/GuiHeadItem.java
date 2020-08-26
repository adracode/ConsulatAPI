package fr.leconsulat.api.gui;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.gui.gui.IGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
import java.util.function.Consumer;

public class GuiHeadItem extends GuiItem {
    
    private String name;
    private Consumer<GuiHeadItem> onUpdate;
    
    public GuiHeadItem(UUID uuid, IGui gui){
        super(Material.PLAYER_HEAD);
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            setPlayer(Bukkit.getOfflinePlayer(uuid));
            gui.update(getSlot());
        });
    }
    
    public GuiHeadItem(String player, IGui gui){
        super(Material.PLAYER_HEAD);
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            setPlayer(Bukkit.getOfflinePlayer(player));
            gui.update(getSlot());
        });
    }
    
    public GuiHeadItem onUpdate(Consumer<GuiHeadItem> onUpdate){
        this.onUpdate = onUpdate;
        return this;
    }
    
    private void setPlayer(OfflinePlayer player){
        if(name != null){
            super.setDisplayName(String.format(name, player.getName()));
        }
        SkullMeta meta = (SkullMeta)getItemMeta();
        meta.setOwningPlayer(player);
        setItemMeta(meta);
        if(onUpdate != null){
            Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                onUpdate.accept(this);
            });
        }
    }
    
    @Override
    public GuiItem setDisplayName(String displayName){
        if(displayName.contains("%s")){
            this.name = displayName;
            return super.setDisplayName(String.format(displayName, "Chargement..."));
        }
        return super.setDisplayName(displayName);
    }
}
