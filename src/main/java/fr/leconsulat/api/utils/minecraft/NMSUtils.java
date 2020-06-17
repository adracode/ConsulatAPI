package fr.leconsulat.api.utils.minecraft;

import com.comphenix.protocol.utility.MinecraftReflection;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSUtils {
    
    private static Method getPlayerHandle;
    private static Field playerConnection;
    private static Method sendPacketPlayer;
    
    private static Constructor<?> blockPositionConstructor;

    private static Method getBlockPosition;

    static {
        try {
            getPlayerHandle = MinecraftReflection.getCraftBukkitClass("entity.CraftPlayer").getMethod("getHandle");
            playerConnection = MinecraftReflection.getMinecraftClass("EntityPlayer").getField("playerConnection");
            sendPacketPlayer = MinecraftReflection.getMinecraftClass("PlayerConnection").getMethod("sendPacket", MinecraftReflection.getMinecraftClass("Packet"));
    
            blockPositionConstructor = MinecraftReflection.getMinecraftClass("BlockPosition").getConstructor(int.class, int.class, int.class);
            
            getBlockPosition = MinecraftReflection.getCraftBukkitClass("block.CraftBlock").getMethod("getPosition");
        } catch(NoSuchMethodException | NoSuchFieldException e){
            e.printStackTrace();
        }
    }
    
    public static String format(String text){
        return "{\"text\":\"" + text + "\"}";
    }
    
    public static Object getBlockPosition(int x, int y, int z){
        try {
            return blockPositionConstructor.newInstance(x, y, z);
        } catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static Object getBlockPosition(Block block){
        try {
            return getBlockPosition.invoke(block);
        } catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static void sendPacket(Player player, Object packet) {
        try {
            Object entityPlayer = getPlayerHandle.invoke(player);
            Object playerConnection = NMSUtils.playerConnection.get(entityPlayer);
            sendPacketPlayer.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
