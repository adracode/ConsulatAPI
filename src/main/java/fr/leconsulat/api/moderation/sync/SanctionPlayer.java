package fr.leconsulat.api.moderation.sync;

import fr.leconsulat.api.moderation.SanctionType;

import java.io.Serializable;
import java.util.UUID;

public class SanctionPlayer implements Serializable {
    
    private static final long serialVersionUID = 1466264488176514207L;
    
    private long id;
    private SanctionType type;
    private UUID uuid;
    private UUID sanctionerUUID;
    private String reason;
    
    public SanctionPlayer(){
    }
    
    public SanctionPlayer(long id, SanctionType type, UUID uuid, String reason, UUID sanctionerUUID){
        this.id = id;
        this.type = type;
        this.uuid = uuid;
        this.reason = reason;
        this.sanctionerUUID = sanctionerUUID;
    }
    
    public SanctionType getType(){
        return type;
    }
    
    public UUID getUUID(){
        return uuid;
    }
    
    public String getReason(){
        return reason;
    }
    
    public UUID getSanctionerUUID(){
        return sanctionerUUID;
    }
    
    public long getId(){
        return id;
    }
}
