package fr.leconsulat.api.player;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.ranks.Rank;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class ConsulatPlayer {
    
    private int id;
    private UUID uuid;
    private Player player;
    private String name;
    private Rank rank;
    private boolean initialized = false;
    private CustomRank customRank;
    private String registered;
    private Gui currentlyOpen;
    private boolean close = true;
    
    public ConsulatPlayer(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
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
        return player == null ? player = Bukkit.getPlayer(uuid) : player;
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
        this.customRank.setColorPrefix(colorPrefix);
    }
    
    public void setPrefix(String prefix){
        this.customRank.setPrefix(prefix);
    }
    
    public void setColorName(ChatColor colorName){
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
        return customRank.getCustomPrefix();
    }
    
    public boolean hasPower(Rank neededRank){
        return this.rank.getRankPower() >= neededRank.getRankPower();
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
        return Objects.hash(uuid);
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
}
