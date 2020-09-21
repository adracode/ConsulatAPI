package fr.leconsulat.api.channel;

import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.redis.RedisManager;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RTopic;

import java.util.UUID;

public class StaffChannel extends Channel implements Speakable {
    
    private RTopic channel = RedisManager.getInstance().getRedis().getTopic("StaffChat");
    
    public StaffChannel(){
        super("staff");
        RedisManager.getInstance().register("StaffChat", String.class, (channel, message) -> {
            super.sendMessage(message);
        });
    }
    
    @Override
    public void sendMessage(@NotNull String message, @NotNull UUID... exclude){
        channel.publishAsync(message);
    }
    
    @Override
    public @NotNull String speak(ConsulatPlayer sender, @NotNull String message){
        return "§c[Staff]§a " + sender.getName() + "§e: " + message;
    }
}
