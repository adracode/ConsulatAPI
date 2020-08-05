package fr.leconsulat.api.channel;

import fr.leconsulat.api.player.ConsulatPlayer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Channel {
    
    private final UUID id;
    private final String name;
    protected Set<ConsulatPlayer> members = new HashSet<>();
    
    public Channel(String id){
        if(ChannelManager.getInstance() == null){
            throw new IllegalStateException("ChannelManager is not instantiated");
        }
        this.id = UUID.randomUUID();
        this.name = id;
        ChannelManager.getInstance().addChannel(id, this);
    }
    
    public UUID getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public boolean isMember(ConsulatPlayer player){
        return members.contains(player);
    }
    
    public void addPlayer(ConsulatPlayer player){
        members.add(player);
    }
    
    public void removePlayer(ConsulatPlayer player){
        if(this.equals(player.getCurrentChannel())){
            player.setCurrentChannel(null);
        }
        members.remove(player);
    }
    
    public boolean isEmpty(){
        return members.isEmpty();
    }
    
    public void sendMessage(String message, UUID... exclude){
        if(exclude.length == 0){
            for(ConsulatPlayer p : members){
                p.sendMessage(message);
            }
            return;
        }
        for(ConsulatPlayer p : members){
            boolean send = true;
            for(UUID uuid : exclude){
                if(p.getUUID().equals(uuid)){
                    send = false;
                    break;
                }
            }
            if(send){
                p.sendMessage(message);
            }
        }
    }
    
    public void sendMessage(TextComponent... message){
        for(ConsulatPlayer p : members){
            p.sendMessage(message);
        }
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Channel)) return false;
        return id.equals(((Channel)o).id);
    }
    
    @Override
    public int hashCode(){
        return id.hashCode();
    }
}
