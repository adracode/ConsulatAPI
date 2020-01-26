package fr.leconsulat.api.runnable;

import fr.leconsulat.api.ConsulatAPI;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KeepAlive implements Runnable {

    @Override
    public void run() {
        try{
            PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT id FROM players");
            preparedStatement.executeQuery();
            preparedStatement.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
