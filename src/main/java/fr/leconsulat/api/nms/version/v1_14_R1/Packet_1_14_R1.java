package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nms.api.Packet;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PacketPlayOutSetSlot;
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
}
