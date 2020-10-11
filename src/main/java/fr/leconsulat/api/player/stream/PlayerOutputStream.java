package fr.leconsulat.api.player.stream;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.utils.InventoryUtils;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PlayerOutputStream {
    
    private ByteArrayOutputStream bytes;
    private ObjectOutputStream os;
    private Player player;
    
    public PlayerOutputStream(Player player){
        this.player = player;
        try {
            os = new ObjectOutputStream(bytes = new ByteArrayOutputStream());
            os.writeObject(player.getUniqueId());
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public PlayerOutputStream writeFully(){
        writeActiveEffects().writeHealth().writeFood().writeSaturation().writeExhaustion().writeFoodTickTimer().writeLevel().writeInventory();
        return this;
    }
    
    public PlayerOutputStream writeHealth(){
        try {
            os.writeFloat((float)player.getHealth());
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public PlayerOutputStream writeFood(){
        try {
            os.writeInt(player.getFoodLevel());
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public PlayerOutputStream writeSaturation(){
        try {
            os.writeFloat(player.getSaturation());
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public PlayerOutputStream writeExhaustion(){
        try {
            os.writeFloat(player.getExhaustion());
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public PlayerOutputStream writeFoodTickTimer(){
        try {
            os.writeInt(ConsulatAPI.getNMS().getPlayer().getFoodTickTimer(player));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public PlayerOutputStream writeLevel(){
        try {
            os.writeFloat(player.getLevel() + player.getExp());
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public PlayerOutputStream writeInventory(){
        try {
            os.writeObject(InventoryUtils.getInventoryAsTag(player.getInventory()));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public PlayerOutputStream writeActiveEffects(){
        try {
            os.writeObject(ConsulatAPI.getNMS().getPlayer().getEffectsAsTag(player));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public byte[] send(){
        try {
            os.close();
            os = null;
            return bytes.toByteArray();
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void finalize() throws Throwable{
        if(os != null){
            os.close();
        }
    }
}
