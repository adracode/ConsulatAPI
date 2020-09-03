package fr.leconsulat.api.channel;

import fr.leconsulat.api.player.ConsulatPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public abstract class Channel implements Iterable<ConsulatPlayer> {
    
    protected final @NotNull Set<ConsulatPlayer> members = new HashSet<>();
    private final @NotNull UUID id = UUID.randomUUID();
    private final @NotNull String name;
    
    public Channel(@NotNull String id){
        this.name = Objects.requireNonNull(id);
        ChannelManager.getInstance().addChannel(id, this);
    }
    
    public boolean isEmpty(){
        return members.isEmpty();
    }
    
    public @NotNull UUID getId(){
        return id;
    }
    
    public @NotNull String getName(){
        return name;
    }
    
    public boolean isMember(@Nullable ConsulatPlayer player){
        return members.contains(player);
    }
    
    public void addPlayer(@NotNull ConsulatPlayer player){
        members.add(Objects.requireNonNull(player));
    }
    
    public void removePlayer(@NotNull ConsulatPlayer player){
        if(this.equals(player.getCurrentChannel())){
            player.setCurrentChannel(null);
        }
        members.remove(player);
    }
    
    public void sendMessage(@NotNull String message, @NotNull UUID... exclude){
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
    
    public void sendMessage(@NotNull TextComponent... message){
        for(ConsulatPlayer p : members){
            p.sendMessage(message);
        }
    }
    
    @Override
    public int hashCode(){
        return id.hashCode();
    }
    
    @Override
    public boolean equals(@Nullable Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof Channel)){
            return false;
        }
        return id.equals(((Channel)o).id);
    }
    
    @Override
    public @NotNull Iterator<ConsulatPlayer> iterator(){
        return new PlayerIterator();
    }
    
    private class PlayerIterator implements Iterator<ConsulatPlayer> {
        
        private final @NotNull Iterator<ConsulatPlayer> iterator = members.iterator();
        
        @Override
        public boolean hasNext(){
            return iterator.hasNext();
        }
        
        @Override
        public @NotNull ConsulatPlayer next(){
            return iterator.next();
        }
    }
}
