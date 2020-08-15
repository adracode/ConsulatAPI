package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nms.api.Packet;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Packet_1_14_R1 implements Packet {
    
    @Override
    public void setSlot(Player player, int slot, ItemStack item){
        EntityPlayer handle = ((CraftPlayer)player).getHandle();
        handle.playerConnection.sendPacket(new PacketPlayOutSetSlot(handle.activeContainer.windowId, slot, CraftItemStack.asNMSCopy(item)));
    }
    
    @Override
    public void tileEntityData(Player player, Location location, int action, CompoundTag tag){
        EntityPlayer handle = ((CraftPlayer)player).getHandle();
        handle.playerConnection.sendPacket(new PacketPlayOutTileEntityData(
                (BlockPosition)ConsulatAPI.getNMS().getBlock().getBlockPosition(location.getBlock()), action, (NBTTagCompound)ConsulatAPI.getNMS().getNBT().toNMS(tag)));
    }
    
    @Override
    public void openSignEditor(Player player, Location location){
        EntityPlayer handle = ((CraftPlayer)player).getHandle();
        handle.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(
                (BlockPosition)ConsulatAPI.getNMS().getBlock().getBlockPosition(location.getBlock())));
    }
    
}
