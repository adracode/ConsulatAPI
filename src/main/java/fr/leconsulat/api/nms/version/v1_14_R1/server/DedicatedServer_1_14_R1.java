package fr.leconsulat.api.nms.version.v1_14_R1.server;

import com.mojang.brigadier.CommandDispatcher;
import fr.leconsulat.api.nms.api.server.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;

public class DedicatedServer_1_14_R1 implements DedicatedServer {
    
    private net.minecraft.server.v1_14_R1.DedicatedServer dedicatedServer;
    
    public DedicatedServer_1_14_R1(){
        this.dedicatedServer = ((CraftServer)Bukkit.getServer()).getServer();
    }
    
    @Override
    public CommandDispatcher<?> getCommandDispatcher(){
        return dedicatedServer.commandDispatcher.a();
    }
    
    @Override
    public CommandDispatcher<?> getVanillaCommandDispatcher(){
        return dedicatedServer.vanillaCommandDispatcher.a();
    }
    
    @Override
    public SimpleCommandMap getCommandMap(){
        return ((CraftServer)Bukkit.getServer()).getCommandMap();
    }
    
    @Override
    public boolean isStopped(){
        return dedicatedServer.hasStopped();
    }
}
