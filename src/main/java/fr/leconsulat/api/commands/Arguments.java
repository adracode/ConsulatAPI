package fr.leconsulat.api.commands;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class Arguments {
    
    private static Constructor<?> argumentProfile;
    private static Method getItemStack;
    private static Method getEnchant;
    
    static{
        try {
            argumentProfile = MinecraftReflection.getMinecraftClass("ArgumentProfile").getConstructor();
            getItemStack = MinecraftReflection.getMinecraftClass("ArgumentItemStack").getMethod("a");
            getEnchant = MinecraftReflection.getMinecraftClass("ArgumentEnchantment").getMethod("a");
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }
    
    public static RequiredArgumentBuilder<Object, ?> enchant(String show){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)getEnchant.invoke(null));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static RequiredArgumentBuilder<Object, ?> item(String show){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)getItemStack.invoke(null));
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static <T> void suggest(Collection<T> suggestions, Function<T, String> toString, Predicate<T> filter, SuggestionsBuilder builder){
        String remaining = builder.getRemaining().toLowerCase();
        for(T suggestion : suggestions){
            if(filter.test(suggestion)){
                String string = toString.apply(suggestion);
                if(string.toLowerCase().startsWith(remaining)){
                    builder.suggest(string);
                }
            }
        }
    }
    
    public static RequiredArgumentBuilder<Object, ?> player(String show){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)argumentProfile.newInstance());
        } catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static RequiredArgumentBuilder<Object, ?> playerList(String show){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)argumentProfile.newInstance()).suggests((context, builder) -> {
                ConsulatPlayer sender = ConsulatCommand.getConsulatPlayerFromContext(context.getSource());
                if(sender != null){
                    suggest(CPlayerManager.getInstance().getConsulatPlayers(),
                            ConsulatPlayer::getName,
                            (player) -> sender.getPlayer().canSee(player.getPlayer()),
                            builder);
                }
                return builder.buildFuture();
            });
        } catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
            throw new RuntimeException();
        }
    }
    
    public static RequiredArgumentBuilder<Object, ?> player(String show, Collection<UUID> list){
        try {
            return RequiredArgumentBuilder.argument(show, (ArgumentType<?>)argumentProfile.newInstance()).suggests((context, builder) -> {
                ConsulatPlayer sender = ConsulatCommand.getConsulatPlayerFromContext(context.getSource());
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
