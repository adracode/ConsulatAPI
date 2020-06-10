package fr.leconsulat.api.channel;

import java.util.HashMap;
import java.util.Map;

public class ChannelManager {
    
    private static ChannelManager instance;
    
    private final Map<String, Channel> channels = new HashMap<>();
    
    public ChannelManager(){
        if(instance == null){
            instance = this;
        } else {
            throw new IllegalStateException("ChannelManager is already instantiated");
        }
    }
    
    public Channel getChannel(String id){
        return channels.get(id);
    }
    
    public void addChannel(String id, Channel channel){
        channels.put(id, channel);
    }
    
    public void removeChannel(String name){
        channels.remove(name);
    }
    
    public static ChannelManager getInstance(){
        return instance;
    }
}
