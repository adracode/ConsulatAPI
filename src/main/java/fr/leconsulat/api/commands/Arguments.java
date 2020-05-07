package fr.leconsulat.api.commands;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;

public class Arguments {
    
    private static Constructor<?> argumentProfile;
    
    /*private static Class<?> commandListenerWrapper;
    
    private static Method getPlayerList;
    private static Method getOperators;*/
    
    static{
        try {
            argumentProfile = MinecraftReflection.getMinecraftClass("ArgumentProfile").getConstructor();
            /*commandListenerWrapper = MinecraftReflection.getMinecraftClass("CommandListenerWrapper");
            getPlayerList = MinecraftReflection.getMinecraftClass("MinecraftServer").getMethod("getPlayerList");
            getOperators = MinecraftReflection.getMinecraftClass("PlayerList").getMethod("m");*/
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }
    
    public static RequiredArgumentBuilder<Object, ?> player(String show){
        try {
            return RequiredArgumentBuilder.argument(show,  (ArgumentType<?>)argumentProfile.newInstance()).suggests((context, builder) -> {
                ConsulatPlayer sender = CPlayerManager.getInstance().getConsulatPlayerFromContext(context.getSource());
                if(sender != null){
                    for(ConsulatPlayer player : CPlayerManager.getInstance().getConsulatPlayers()){
                        if(sender.getPlayer().canSee(player.getPlayer()) && player.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())){
                            builder.suggest(player.getName());
                        }
                    }
                }
                return builder.buildFuture();
            });
        } catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static RequiredArgumentBuilder<Object, ?> player(String show, Collection<UUID> list){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)argumentProfile.newInstance()).suggests((context, builder) -> {
                ConsulatPlayer sender = CPlayerManager.getInstance().getConsulatPlayerFromContext(context.getSource());
                if(sender != null){
                    for(UUID uuid : list){
                        Player player = Bukkit.getPlayer(uuid);
                        if(player != null){
                            if(sender.getPlayer().canSee(player) && player.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())){
                                builder.suggest(player.getName());
                            }
                        }
                    }
                }
                return builder.buildFuture();
            });
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static RequiredArgumentBuilder<Object, ?> word(String show){
        return RequiredArgumentBuilder.argument(show, StringArgumentType.word());
    }
}
