package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nms.api.Inventory;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PacketPlayOutSetSlot;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Inventory_1_14_R1 implements Inventory {
    
    @Override
    public void fakeItem(Player player, int slot, ItemStack item){
        EntityPlayer handlePlayer = ((CraftPlayer)player).getHandle();
        handlePlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(
                handlePlayer.activeContainer.windowId, slot, CraftItemStack.asNMSCopy(item)));
    }
    
}
