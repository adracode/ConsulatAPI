package fr.leconsulat.api.moderation.sync;

import fr.leconsulat.api.moderation.SanctionType;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class SanctionConfirm implements Serializable {
    
    private static final long serialVersionUID = -7271772701713889267L;
    
    private static final Random RANDOM = new Random();
    
    private long id;
    private UUID sanctioned;
    private UUID sanctioner;
    private SanctionType type;
    private boolean applied;
    private transient int channelsReceived = -1;
    private transient int currentReceived = 0;
    private transient Consumer<Boolean> onComplete;
    private transient boolean completed = false;
    
    public SanctionConfirm(){
    }
    
    public SanctionConfirm(UUID sanctioned, UUID sanctioner, SanctionType type, boolean applied){
        this(RANDOM.nextLong(), sanctioned, sanctioner, type, applied);
    }
    
    public SanctionConfirm(long id, UUID sanctioned, UUID sanctioner, SanctionType type, boolean applied){
        this.id = id;
        this.sanctioned = sanctioned;
        this.sanctioner = sanctioner;
        this.type = type;
        this.applied = applied;
    }
    
    public long getId(){
        return id;
    }
    
    public UUID getSanctioned(){
        return sanctioned;
    }
    
    public UUID getSanctioner(){
        return sanctioner;
    }
    
    public SanctionType getType(){
        return type;
    }
    
    public boolean isApplied(){
        return applied;
    }
    
    public void setChannelsReceived(int channelsReceived){
        this.channelsReceived = channelsReceived;
        if(onComplete != null && !completed && currentReceived == channelsReceived){
            onComplete.accept(applied);
            completed = true;
        }
    }
    
    public void received(boolean applied){
        ++currentReceived;
        if(applied){
            this.applied = true;
        }
        if(onComplete != null && !completed && currentReceived == channelsReceived){
            onComplete.accept(this.applied);
            completed = true;
        }
    }
    
    public void setOnComplete(Consumer<Boolean> onComplete){
        this.onComplete = onComplete;
        if(!completed && currentReceived == channelsReceived){
            onComplete.accept(applied);
            completed = true;
        }
    }
    
    @Override
    public String toString(){
        return "SanctionConfirm{" +
                "id=" + id +
                ", sanctioned=" + sanctioned +
                ", sanctioner=" + sanctioner +
                ", type=" + type +
                ", applied=" + applied +
                ", channelsReceived=" + channelsReceived +
                ", currentReceived=" + currentReceived +
                ", onComplete=" + onComplete +
                ", completed=" + completed +
                '}';
    }
}
