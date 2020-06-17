package fr.leconsulat.api.utils.minecraft.packets;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.utils.minecraft.NMSUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class PacketUtils {
    
    private static Constructor<?> openSignEditorPacketConstructor;
    private static Constructor<?> tileEntityDataConstructor;
    
    private static Field updateSignLines;
    
    static {
        try {
            openSignEditorPacketConstructor = MinecraftReflection.getMinecraftClass("PacketPlayOutOpenSignEditor")
                    .getConstructor(MinecraftReflection.getMinecraftClass("BlockPosition"));
            tileEntityDataConstructor = MinecraftReflection.getMinecraftClass("PacketPlayOutTileEntityData")
                    .getConstructor(MinecraftReflection.getMinecraftClass("BlockPosition"), int.class, MinecraftReflection.getNBTCompoundClass());
    
            updateSignLines = MinecraftReflection.getMinecraftClass("PacketPlayInUpdateSign").getDeclaredField("b");
            updateSignLines.setAccessible(true);
        } catch(NoSuchMethodException | NoSuchFieldException e){
            e.printStackTrace();
        }
    }
    
    public static void sendOpenSignEditorPacket(Player player, Block block){
        try {
            Object packet = openSignEditorPacketConstructor.newInstance(NMSUtils.getBlockPosition(block));
            NMSUtils.sendPacket(player, packet);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
    
    public static void sendOpenSignEditorPacket(Player player, Location location){
        try {
            Object packet = openSignEditorPacketConstructor.newInstance(NMSUtils.getBlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            NMSUtils.sendPacket(player, packet);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
    
    public static void sendTileEntityDataPacket(Player player, Location location, int action, Object compoundTag){
        try {
            Object packet = tileEntityDataConstructor.newInstance(NMSUtils.getBlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), action, compoundTag);
            NMSUtils.sendPacket(player, packet);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
    
    public static String[] getArrayFromUpdateSignPacket(Object packet){
        try {
            return (String[])updateSignLines.get(packet);
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
        return new String[4];
    }
    
}
