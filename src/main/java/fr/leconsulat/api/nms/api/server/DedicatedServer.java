package fr.leconsulat.api.nms.api.server;

import com.mojang.brigadier.CommandDispatcher;
import org.bukkit.command.SimpleCommandMap;

public interface DedicatedServer {
    
    CommandDispatcher<?> getCommandDispatcher();
    
    CommandDispatcher<?> getVanillaCommandDispatcher();
    
    SimpleCommandMap getCommandMap();
    
    boolean isStopped();
    
    boolean getProperties(String properties);
    
    void setProperties(String properties, boolean value);
    
}
