package fr.leconsulat.api.player.stream;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.NBTInputStream;
import fr.leconsulat.api.nbt.NBTType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class OfflinePlayerOutputStream {
    
    private ByteArrayOutputStream bytes;
    private ObjectOutputStream os;
    private CompoundTag player;
    
    public OfflinePlayerOutputStream(UUID uuid){
        try {
            this.player = new NBTInputStream(ConsulatAPI.getConsulatAPI().getPlayerFile(uuid)).read();
            os = new ObjectOutputStream(bytes = new ByteArrayOutputStream());
            os.writeObject(uuid);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public OfflinePlayerOutputStream writeLevel(){
        try {
            os.writeFloat(player.getInt("XpLevel") + player.getFloat("XpP"));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public OfflinePlayerOutputStream writeInventory(){
        try {
            os.writeObject(player.getListTag("Inventory", NBTType.COMPOUND));
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
