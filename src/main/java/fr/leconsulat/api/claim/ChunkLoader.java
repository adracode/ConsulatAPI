package fr.leconsulat.api.claim;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.player.PlayersManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChunkLoader {

    public static List<ClaimObject> claims = new ArrayList<>();

    public static void init() throws SQLException {
        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT * FROM claims");
        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            ClaimObject claimObject = new ClaimObject(resultSet.getInt("claim_x"), resultSet.getInt("claim_z"), resultSet.getString("player_uuid"));
            PreparedStatement accessStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT * FROM access WHERE claim_x = ? AND claim_z = ?");
            accessStatement.setInt(1, resultSet.getInt("claim_x"));
            accessStatement.setInt(2, resultSet.getInt("claim_z"));
            ResultSet accessSet = accessStatement.executeQuery();
            while(accessSet.next()) {
                claimObject.access.add(accessSet.getString("player_uuid"));
            }
            accessSet.close();
            accessStatement.close();
            ChunkLoader.claims.add(claimObject);
        }

        preparedStatement.close();
    }

    public static ClaimObject getClaimedZone(Chunk chunk) {
        for(ClaimObject claim : claims) {
            if(claim.getX() == chunk.getX() && claim.getZ() == chunk.getZ()) {
                return claim;
            }
        }
        return null;
    }

    public static boolean isClaimed(Chunk chunk) {
        for(ClaimObject claim : claims) {
            return (claim.getX() == chunk.getX() && claim.getZ() == chunk.getZ());
        }
        return false;
    }

    public static void claim(Player player) throws SQLException {
        ConsulatPlayer consulatPlayer = PlayersManager.getConsulatPlayer(Bukkit.getPlayer(player.getName()));
        Chunk myChunk = player.getWorld().getChunkAt(player.getLocation());
        int x = myChunk.getX();
        int z = myChunk.getZ();

        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("INSERT INTO claims(claim_x, claim_z, player_uuid) VALUES(?, ?, ?)");
        preparedStatement.setInt(1, x);
        preparedStatement.setInt(2, z);
        preparedStatement.setString(3, player.getUniqueId().toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        claims.add(new ClaimObject(x, z, player.getUniqueId().toString()));
    }

    public static void unclaim(int x, int z) throws SQLException {
        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("DELETE FROM claims WHERE claim_x = ? AND claim_z = ?");
        preparedStatement.setInt(1, x);
        preparedStatement.setInt(2, z);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
