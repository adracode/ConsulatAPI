package fr.leconsulat.api.custom;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomDatabase {

    public static CustomObject getCustom(Player player) throws SQLException {
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("SELECT buyedPerso,prefix_perso FROM players WHERE player_name = ?");
        request.setString(1, player.getName());
        ResultSet resultSet = request.executeQuery();
        if(resultSet.next()) {
            boolean hasCustom = resultSet.getBoolean("buyedPerso");
            String prefix = resultSet.getString("prefix_perso");
            resultSet.close();
            request.close();
            return new CustomObject(hasCustom, prefix);
        }
        return new CustomObject(false, "");
    }

    public static void activePerso(Player player) throws SQLException {
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET buyedPerso  = 1 WHERE player_name = ?");
        request.setString(1, player.getName());
        request.executeUpdate();
        request.close();
}

    public static void setPrefix(Player player, String prefix) throws SQLException {
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET prefix_perso = ? WHERE player_name = ?");
        request.setString(1, prefix);
        request.setString(2, player.getName());
        request.executeUpdate();
        request.close();
    }
}
