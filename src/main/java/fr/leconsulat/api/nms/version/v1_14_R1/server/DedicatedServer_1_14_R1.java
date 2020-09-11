package fr.leconsulat.api.nms.version.v1_14_R1.server;

import com.mojang.brigadier.CommandDispatcher;
import fr.leconsulat.api.nms.api.server.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DedicatedServer_1_14_R1 implements DedicatedServer {
    
    private static final Method SAVE_CONFIG;
    
    static{
        try {
            SAVE_CONFIG = CraftServer.class.getDeclaredMethod("saveConfig");
            SAVE_CONFIG.setAccessible(true);
        } catch(NoSuchMethodException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    private net.minecraft.server.v1_14_R1.DedicatedServer dedicatedServer =
            ((CraftServer)Bukkit.getServer()).getServer();
    private YamlConfiguration bukkitConfig;
    
    public DedicatedServer_1_14_R1(){
        try {
            Field configuration = CraftServer.class.getDeclaredField("configuration");
            configuration.setAccessible(true);
            bukkitConfig = (YamlConfiguration)configuration.get(Bukkit.getServer());
        } catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
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
    
    @Override
    public boolean getProperties(String properties){
        return bukkitConfig.getBoolean(properties);
    }
    
    @Override
    public void setProperties(String properties, boolean value){
        bukkitConfig.set(properties, value);
        try {
            SAVE_CONFIG.invoke(Bukkit.getServer());
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
