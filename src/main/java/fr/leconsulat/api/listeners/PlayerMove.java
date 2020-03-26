package fr.leconsulat.api.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

/**
 * Created by KIZAFOX on 13/03/2020 for ConsulatAPI
 */
public class PlayerMove implements Listener {

    @EventHandler
    public void enterLeaveZone(PlayerMoveEvent event) {
        Chunk chunkFrom = event.getFrom().getChunk();
        Chunk chunkTo = Objects.requireNonNull(event.getTo()).getChunk();
        Player player = event.getPlayer();

        if(player.getWorld() != Bukkit.getWorlds().get(0)) return;

        if(!chunkFrom.equals(chunkTo)) {
            Bukkit.getPluginManager().callEvent(new ChunkChangeEvent(player, chunkFrom, chunkTo));
        }
    }
}
