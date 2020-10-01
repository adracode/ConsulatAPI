package fr.leconsulat.api.player.stream;

import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.ListTag;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.UUID;

public class OfflinePlayerInputStream {
    
    private ObjectInputStream is;
    
    public OfflinePlayerInputStream(@NotNull byte[] data){
        try {
            is = new ObjectInputStream(new ByteArrayInputStream(Objects.requireNonNull(data)));
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public UUID fetchUUID(){
        try {
            return (UUID)is.readObject();
        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public float fetchHealth(){
        try {
            return is.readFloat();
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }
    
    public int fetchFood(){
        try {
            return is.readInt();
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }
    
    public float fetchSaturation(){
        try {
            return is.readFloat();
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }
    
    public float fetchExhaustion(){
        try {
            return is.readFloat();
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }
    
    public int fetchFoodTickTimer(){
        try {
            return is.readInt();
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }
    
    public float fetchLevel(){
        try {
            return is.readFloat();
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }
    
    @SuppressWarnings("unchecked")
    public ListTag<CompoundTag> fetchInventory(){
        try {
            return (ListTag<CompoundTag>)is.readObject();
        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public ListTag<CompoundTag> fetchActiveEffects(){
        try {
            return (ListTag<CompoundTag>)is.readObject();
        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public void close(){
        try {
            is.close();
            is = null;
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    @Override
    protected void finalize() throws Throwable{
        if(is != null){
            is.close();
        }
    }
}
