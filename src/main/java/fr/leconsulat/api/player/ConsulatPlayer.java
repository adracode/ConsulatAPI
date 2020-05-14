package fr.leconsulat.api.player;

import fr.leconsulat.api.database.Saveable;
import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.ranks.Rank;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class ConsulatPlayer implements Saveable {
    
    private int id;
    private final UUID uuid;
    private final Player player;
    private final String name;
    private Rank rank;
    private boolean initialized = false;
    private CustomRank customRank;
    private String registered;
    private Gui currentlyOpen;
    private boolean vanished;
    
    public ConsulatPlayer(UUID uuid, String name){
        if(uuid == null || name == null){
            throw new NullPointerException("Consulat Player cannot be instantiated with null arguments");
        }
        this.uuid = uuid;
        this.name = name;
        this.player = Bukkit.getPlayer(uuid);
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
    }
    
    public void initialize(int id, Rank rank, boolean hasCustomRank, String customRank, String registered){
        this.id = id;
        this.rank = rank;
        this.customRank = hasCustomRank ? new CustomRank(customRank) : null;
        this.registered = registered;
        this.initialized = true;
    }
    
    public int getId(){
        return id;
    }
    
    public UUID getUUID(){
        return uuid;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    public Rank getRank(){
        return rank;
    }
    
    public void setRank(Rank rank) throws SQLException{
        CPlayerManager.getInstance().setRank(getUUID(), rank);
        this.rank = rank;
    }
    
    public boolean isInitialized(){
        return initialized;
    }
    
    public boolean hasCustomRank(){
        return customRank != null;
    }
    
    public void setHasCustomRank(boolean hasCustomRank) throws SQLException{
        CPlayerManager.getInstance().setHasCustomRank(getUUID(), hasCustomRank);
        if(hasCustomRank){
            customRank = new CustomRank();
        } else {
            customRank = null;
        }
    }
    
    public void setColorPrefix(ChatColor colorPrefix){
        if(!hasCustomRank()){
            return;
        }
        this.customRank.setColorPrefix(colorPrefix);
    }
    
    public void setPrefix(String prefix){
        if(!hasCustomRank()){
            return;
        }
        this.customRank.setPrefix(prefix);
    }
    
    public void setColorName(ChatColor colorName){
        if(!hasCustomRank()){
            return;
        }
        this.customRank.setColorName(colorName);
    }
    
    public void resetCustomRank() throws SQLException{
        if(!hasCustomRank()){
            return;
        }
        CPlayerManager.getInstance().setCustomRank(getUUID(), null);
        this.customRank.reset();
    }
    
    public void applyCustomRank() throws SQLException{
        if(!hasCustomRank()){
            return;
        }
        CPlayerManager.getInstance().setCustomRank(getUUID(), customRank.getCustomRank().replace('§', '&'));
    }
    
    public String getCustomRank(){
        return hasCustomRank() ? customRank.getCustomRank() : null;
    }
    
    public String getCustomPrefix(){
        return hasCustomRank() ? customRank.getCustomPrefix() : null;
    }
    
    public boolean hasPower(Rank neededRank){
        return this.rank != null && this.rank.getRankPower() >= neededRank.getRankPower();
    }
    
    public String getName(){
        return name;
    }
    
    public String getRegistered(){
        return registered;
    }
    
    public void sendMessage(String message){
        getPlayer().sendMessage(message);
    }
    
    public void sendMessage(TextComponent... message){
        getPlayer().spigot().sendMessage(message);
    }
    
    public boolean isVanished(){
        return vanished;
    }
    
    public void setVanished(boolean vanished){
        this.vanished = vanished;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof ConsulatPlayer)){
            return false;
        }
        return uuid.equals(((ConsulatPlayer)o).uuid);
    }
    
    @Override
    public int hashCode(){
        return uuid.hashCode();
    }
    
    @Override
    public String toString(){
        return "ConsulatPlayer{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", player=" + player +
                ", name='" + name + '\'' +
                ", rank=" + rank +
                ", initialized=" + initialized +
                ", customRank=" + customRank +
                ", registered='" + registered + '\'' +
                '}';
    }
    
    public Gui getCurrentlyOpen(){
        return currentlyOpen;
    }
    
    public void setCurrentlyOpen(Gui gui){
        this.currentlyOpen = gui;
    }
    
    @Override
    public String getDatabaseIdentifier(){
        return uuid.toString();
    }
}
