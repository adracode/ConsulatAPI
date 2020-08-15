package fr.leconsulat.api.channel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class ChannelManager {
    
    private static ChannelManager instance;
    
    static{
        new ChannelManager();
    }
    
    private final @NotNull Map<String, Channel> channels = new HashMap<>();
    
    private ChannelManager(){
        if(instance == null){
            instance = this;
        } else {
            throw new IllegalStateException("ChannelManager is already instantiated");
        }
    }
    
    public @NotNull Channel getChannel(@NotNull String id){
        return Objects.requireNonNull(channels.get(id));
    }
    
    public void addChannel(@NotNull String id, @NotNull Channel channel){
        channels.put(id, channel);
    }
    
    public void removeChannel(@Nullable String name){
        channels.remove(name);
    }
    
    public static @NotNull ChannelManager getInstance(){
        return instance;
    }
}
