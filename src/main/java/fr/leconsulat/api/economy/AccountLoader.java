package fr.leconsulat.api.economy;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.player.PlayersManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class AccountLoader {

    public static boolean hasAccount(OfflinePlayer p) throws SQLException {
        final PreparedStatement q = ConsulatAPI.getDatabase().prepareStatement("SELECT id FROM players WHERE player_uuid = ?");
        q.setString(1, p.getUniqueId().toString());
        q.executeQuery();
        final ResultSet resultat = q.executeQuery();
        final boolean hasAccount = resultat.next();
        q.close();
        return hasAccount;
    }

    public static int hasSupplementHome(OfflinePlayer p) throws SQLException {
        final PreparedStatement q = ConsulatAPI.getDatabase().prepareStatement("SELECT moreHomes FROM players WHERE player_uuid = ?");
        q.setString(1, p.getUniqueId().toString());
        q.executeQuery();
        final ResultSet resultat = q.executeQuery();
        int hasAccount = 0;
        while(resultat.next()) {
            hasAccount = resultat.getInt("moreHomes");
        }
        q.close();
        return hasAccount;
    }

    public static boolean hasBuyedHome(String player) throws SQLException {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        int x = hasSupplementHome(op);
        if(x == 0) {
            return false;
        } else {
            return true;
        }
    }
    public static void addHome(String player) throws SQLException {
        if(!hasBuyedHome(player)) {
            try {
                OfflinePlayer op = Bukkit.getOfflinePlayer(player);
                final PreparedStatement rs = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET moreHomes = ? WHERE player_uuid = ?");
                rs.setInt(1, 1);
                rs.setString(2, op.getUniqueId().toString());
                rs.executeUpdate();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getAmountOfShop(String p) throws SQLException {
        OfflinePlayer op = Bukkit.getOfflinePlayer(p);

        final PreparedStatement q = ConsulatAPI.getDatabase().prepareStatement("SELECT Shops FROM players WHERE player_uuid = ?");
        q.setString(1, op.getUniqueId().toString());
        q.executeQuery();
        final ResultSet resultat = q.executeQuery();
        int hasAccount = 0;
        while(resultat.next()) {
            hasAccount = resultat.getInt("Shops");
        }
        q.close();
        return hasAccount;
    }

    public static void addShopToBdd(int x, int y, int z, String mat, Double price, OfflinePlayer offlinePlayer) throws SQLException {
        PreparedStatement rs;
        rs = ConsulatAPI.getDatabase().prepareStatement("INSERT INTO shopinfo(shop_x, shop_y, shop_z, material, price, owner_uuid) VALUES(?, ?, ?, ?, ?, ?)");
        rs.setInt(1, x);
        rs.setInt(2, y);
        rs.setInt(3, z);
        rs.setString(4, mat);
        rs.setDouble(5, price);
        rs.setString(6, offlinePlayer.getUniqueId().toString());
        rs.executeUpdate();
        rs.close();
    }

    public static void removeShopFromBdd(int x, int y, int z, String mat, Double price, OfflinePlayer offlinePlayer) throws SQLException {
        PreparedStatement rs;
        rs = ConsulatAPI.getDatabase().prepareStatement("DELETE FROM shopinfo WHERE shop_x = ? AND shop_y = ? AND shop_z = ? AND material = ? AND price = ? AND owner_uuid = ?;");
        rs.setInt(1, x);
        rs.setInt(2, y);
        rs.setInt(3, z);
        rs.setString(4, mat);
        rs.setDouble(5, price);
        rs.setString(6, offlinePlayer.getUniqueId().toString());
        rs.executeUpdate();
        rs.close();
    }

    public static ArrayList<ShopInfo> getShopList() throws SQLException {
        ArrayList<ShopInfo> allShops = new ArrayList<>();
        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT * FROM shopinfo WHERE isEmpty = 0");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            allShops.add(new ShopInfo(resultSet.getInt("shop_x"), resultSet.getInt("shop_y"), resultSet.getInt("shop_z"), resultSet.getString("material"), resultSet.getDouble("price"), resultSet.getString("owner_uuid")));
        }

        preparedStatement.close();
        return allShops;
    }

    public static void addShops(String player) {
        try {
            OfflinePlayer op = Bukkit.getOfflinePlayer(player);

            final PreparedStatement rs = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET Shops = ? WHERE player_uuid = ?");
            rs.setInt(1, getAmountOfShop(player) + 1);
            rs.setString(2, op.getUniqueId().toString());
            rs.executeUpdate();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void remShops(String player) {
        try {
            OfflinePlayer op = Bukkit.getOfflinePlayer(player);

            final PreparedStatement rs = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET Shops = ? WHERE player_uuid = ?");
            rs.setInt(1, getAmountOfShop(player) - 1);
            rs.setString(2, op.getUniqueId().toString());
            rs.executeUpdate();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Claim> getClaims(Player player) throws SQLException {
        ArrayList<Claim> myClaims = new ArrayList<>();
        ArrayList<String> accessed;

        ConsulatPlayer consulatPlayer = PlayersManager.getConsulatPlayer(player);

        final PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT `claim_x`, `claim_z`  FROM `claims` WHERE `player_uuid` = ?");
        preparedStatement.setString(1, player.getUniqueId().toString());
        preparedStatement.executeQuery();
        final ResultSet resultat = preparedStatement.executeQuery();
        while(resultat.next()) {
            int x = resultat.getInt("claim_x");
            int z = resultat.getInt("claim_z");
            Chunk c = Objects.requireNonNull(Bukkit.getWorld("world")).getChunkAt(x, z);
            accessed = getAccessedOfAClaim(c);
            myClaims.add(new Claim(x, z, accessed));
        }
        preparedStatement.close();
        return myClaims;
    }

    public static ArrayList<String> getAccessedOfAClaim(Chunk chunk) throws SQLException {
        ArrayList<String> accessed = new ArrayList<>();

        final PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT `player_uuid` FROM `access` WHERE `claim_x` = ? AND `claim_z` = ?");
        preparedStatement.setInt(1, chunk.getX());
        preparedStatement.setInt(2, chunk.getZ());
        preparedStatement.executeQuery();
        final ResultSet resultat = preparedStatement.executeQuery();

        while(resultat.next()) {
            String uuid = resultat.getString("player_uuid");
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            accessed.add(offlinePlayer.getName());
        }
        preparedStatement.close();
        return accessed;
    }

    public static void updateMoney(String playerName, double addMoney){
        try {
            PreparedStatement rs = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET money = money + ? WHERE player_name = ?");
            rs.setDouble(1, addMoney);
            rs.setString(2, playerName);
            rs.executeUpdate();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
