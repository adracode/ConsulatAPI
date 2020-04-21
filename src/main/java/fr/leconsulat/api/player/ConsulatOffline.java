package fr.leconsulat.api.player;

import fr.leconsulat.api.ranks.Rank;

import java.util.UUID;

public class ConsulatOffline {

    private int id;
    private UUID uuid;
    private String name;
    private Rank rank;
    private String registered;
    private String lastConnection;
    
    public ConsulatOffline(int id, UUID uuid, String name, Rank rank, String registered){
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.registered = registered;
    }
    
    public int getId(){
        return id;
    }
    
    public UUID getUUID(){
        return uuid;
    }
    
    public String getName(){
        return name;
    }
    
    public Rank getRank(){
        return rank;
    }
    
    public String getRegistered(){
        return registered;
    }
    
    public String getLastConnection(){
        return lastConnection;
    }
    
    public void setLastConnection(String lastConnection){
        this.lastConnection = lastConnection;
    }
}


