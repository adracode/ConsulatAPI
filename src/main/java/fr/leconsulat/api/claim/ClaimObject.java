package fr.leconsulat.api.claim;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimObject {

    private String playerName;

    private int X;
    private int Z;
    private String playerUUID;
    public List<String> access;

    public ClaimObject(int x, int z, String playerUUID)  {
        this.X = x;
        this.Z = z;
        this.access = new ArrayList<>();
        this.playerUUID = playerUUID;
    }

    public int getX() {
        return X;
    }

    public int getZ() {
        return Z;
    }

    public String getPlayerUUID()
    {
        return playerUUID;
    }

    public String getPlayerName()
    {
        return Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName();
    }

    public boolean isInArea(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        return chunk.getX() == this.X && chunk.getZ() == Z;
    }

}
