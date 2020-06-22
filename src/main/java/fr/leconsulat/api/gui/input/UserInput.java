package fr.leconsulat.api.gui.input;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.utils.minecraft.NMSUtils;
import fr.leconsulat.api.utils.minecraft.nbt.NBTMinecraft;
import fr.leconsulat.api.utils.minecraft.packets.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public final class UserInput {
    
    private String[] defaultText;
    private Consumer<String> processInput;
    private int[] inputs;
    
    public UserInput(Consumer<String> consumer, String[] defaultText, int[] inputs){
        this.processInput = consumer;
        this.defaultText = defaultText;
        this.inputs = inputs;
    }
    
    public void processInput(String[] input){
        StringBuilder result = new StringBuilder();
        for(int i : inputs){
            result.append(input[i]);
        }
        processInput.accept(result.toString());
    }
    
    public void open(Player player){
        Location location = player.getLocation();
        location = location.clone();
        location.setY(location.getBlockY() > 10 ? 1 : 255);
        
        Object signNBT = NBTMinecraft.newCompoundTag();
        NBTMinecraft.setCompoundTagString(signNBT, "id", "minecraft:sign");
        NBTMinecraft.setCompoundTagInt(signNBT, "x", location.getBlockX());
        NBTMinecraft.setCompoundTagInt(signNBT, "y", location.getBlockY());
        NBTMinecraft.setCompoundTagInt(signNBT, "z", location.getBlockZ());
        for(int i = 0; i < 4; i++){
            NBTMinecraft.setCompoundTagString(signNBT, "Text" + (i + 1), NMSUtils.format(defaultText.length > i ? defaultText[i] : ""));
        }
        player.sendBlockChange(location, Material.OAK_SIGN.createBlockData());
        PacketUtils.sendTileEntityDataPacket(player, location, 9, signNBT);
        PacketUtils.sendOpenSignEditorPacket(player, location);
        Location finalLocation = location;
        Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), ()->
                player.sendBlockChange(finalLocation, finalLocation.getWorld().getBlockAt(finalLocation).getBlockData()), 20L);
    }
}