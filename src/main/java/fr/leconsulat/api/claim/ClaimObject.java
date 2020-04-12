package fr.leconsulat.api.claim;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimObject {

    private String playerName;

    private int X;
    private int Z;
    private String playerUUID;
    public List<String> access;
    private String description;

    public ClaimObject(int x, int z, List<String> access, String playerUUID, String description)  {
        this.X = x;
        this.Z = z;
        this.access = access;
        this.playerUUID = playerUUID;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
