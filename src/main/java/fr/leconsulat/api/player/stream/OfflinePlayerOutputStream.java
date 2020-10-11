package fr.leconsulat.api.player.stream;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.ListTag;
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
    
    public OfflinePlayerOutputStream writeFully(){
        writeActiveEffects().writeHealth().writeFood().writeSaturation().writeExhaustion().writeFoodTickTimer().writeLevel().writeInventory();
        return this;
    }
    
    public OfflinePlayerOutputStream writeHealth(){
        try {
            os.writeFloat(player.getFloat("Health"));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public OfflinePlayerOutputStream writeFood(){
        try {
            os.writeInt(player.getInt("foodLevel"));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public OfflinePlayerOutputStream writeSaturation(){
        try {
            os.writeFloat(player.getFloat("foodSaturationLevel"));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public OfflinePlayerOutputStream writeExhaustion(){
        try {
            os.writeFloat(player.getFloat("foodExhaustionLevel"));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public OfflinePlayerOutputStream writeFoodTickTimer(){
        try {
            os.writeInt(player.getInt("foodTickTimer"));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
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
            os.writeObject(player.has("Inventory") ? player.getListTag("Inventory", NBTType.COMPOUND) : new ListTag<>(NBTType.COMPOUND));
        } catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public OfflinePlayerOutputStream writeActiveEffects(){
        try {
            os.writeObject(player.has("ActiveEffects") ? player.getListTag("ActiveEffects", NBTType.COMPOUND) : new ListTag<>(NBTType.COMPOUND));
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
