package fr.leconsulat.api.gui.input;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nms.api.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class UserInput {
    
    private @NotNull String[] defaultText;
    private @NotNull Consumer<String> processInput;
    private @NotNull int[] inputs;
    
    public UserInput(@NotNull Consumer<String> consumer, @NotNull String[] defaultText, @NotNull int[] inputs){
        this.processInput = Objects.requireNonNull(consumer, "onUserInput");
        this.defaultText = Objects.requireNonNull(defaultText);
        this.inputs = Objects.requireNonNull(inputs);
    }
    
    public void processInput(String[] input){
        StringBuilder result = new StringBuilder();
        for(int i : inputs){
            result.append(input[i]);
        }
        processInput.accept(result.toString());
    }
    
    public void open(Player player){
        Packet packetNMS = ConsulatAPI.getNMS().getPacket();
        Location location = player.getLocation();
        location = location.clone();
        location.setY(location.getBlockY() > 10 ? 0 : 255);
        CompoundTag signNBT = new CompoundTag();
        signNBT.putString("id", "minecraft:sign");
        signNBT.putInt("x", location.getBlockX());
        signNBT.putInt("y", location.getBlockY());
        signNBT.putInt("z", location.getBlockZ());
        for(int i = 0; i < 4; i++){
            signNBT.putString("Text" + (i + 1), "{\"text\":\"" + (defaultText.length > i ? defaultText[i] : "") + "\"}");
        }
        player.sendBlockChange(location, Material.OAK_SIGN.createBlockData());
        packetNMS.tileEntityData(player, location, 9, signNBT);
        packetNMS.openSignEditor(player, location);
        Location finalLocation = location;
        Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () ->
                player.sendBlockChange(finalLocation, finalLocation.getWorld().getBlockAt(finalLocation).getBlockData()), 20L);
    }
}