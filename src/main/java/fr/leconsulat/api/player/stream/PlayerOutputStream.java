package fr.leconsulat.api.player.stream;

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
