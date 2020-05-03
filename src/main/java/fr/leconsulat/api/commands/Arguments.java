package fr.leconsulat.api.commands;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Arguments {
    
    private static Constructor argumentProfile;
    
    private static Class<?> commandListenerWrapper;
    
    private static Method getPlayerList;
    private static Method getOperators;
    
    static{
        try {
            argumentProfile = MinecraftReflection.getMinecraftClass("ArgumentProfile").getConstructor();
            commandListenerWrapper = MinecraftReflection.getMinecraftClass("CommandListenerWrapper");
            getPlayerList = MinecraftReflection.getMinecraftClass("MinecraftServer").getMethod("getPlayerList");
            getOperators = MinecraftReflection.getMinecraftClass("PlayerList").getMethod("m");
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }
    
    public static RequiredArgumentBuilder<Object, ?> operators(String show){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)argumentProfile.newInstance()).suggests((context, builder) -> {
                try {
                    return a((String[])getOperators.invoke(getPlayerList.invoke(ConsulatAPI.getConsulatAPI().getDedicatedServer())), builder);
                } catch(IllegalAccessException | InvocationTargetException e){
                    e.printStackTrace();
                }
                return null;
            });
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static RequiredArgumentBuilder<Object, ?> player(String show){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)argumentProfile.newInstance()).suggests((context, builder) -> {
                for(Player player : Bukkit.getOnlinePlayers()){
                    builder.suggest(player.getName());
                }
                return builder.buildFuture();
            });
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static RequiredArgumentBuilder<Object, ?> player(String show, Collection<UUID> list){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)argumentProfile.newInstance()).suggests((context, builder) -> {
                for(UUID uuid : list){
                    Player player = Bukkit.getPlayer(uuid);
                    if(player != null){
                        builder.suggest(player.getName());
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
    
    private static CompletableFuture<Suggestions> a(String[] args, SuggestionsBuilder builder){
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        for(String arg : args){
            if(arg.toLowerCase(Locale.ROOT).startsWith(remaining)){
                builder.suggest(arg);
            }
        }
        return builder.buildFuture();
    }
    
}
