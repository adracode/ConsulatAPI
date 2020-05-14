package fr.leconsulat.api.database;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SaveTask implements Runnable {
    
    private String sqlStatement;
    private Field field;
    private Map<Saveable, Object> lastDataSaved = new HashMap<>();
    
    public SaveTask(String table, String idColumn, String column, Class<?> c, String field){
        this.sqlStatement = "UPDATE " + table + " SET " + column + " = ? WHERE " + idColumn + " = ?;";
        try {
            this.field = c.getDeclaredField(field);
            this.field.setAccessible(true);
        } catch(NoSuchFieldException e){
            e.printStackTrace();
        }
    }
    
    @Override
    public void run(){
        try {
            PreparedStatement update = null;
            for(Map.Entry<Saveable, Object> toSave : lastDataSaved.entrySet()){
                Object data = toSave.getValue();
                Object currentData = field.get(toSave.getKey());
                //System.out.println(data);
                //System.out.println(currentData);
                if(!data.equals(currentData)){
                    if(update == null){
                        update = ConsulatAPI.getDatabase().prepareStatement(sqlStatement);
                    }
                    //System.out.println(sqlStatement);
                    update.setObject(1, currentData);
                    lastDataSaved.replace(toSave.getKey(), currentData);
                    update.setString(2, toSave.getKey().getDatabaseIdentifier());
                    update.addBatch();
                }
            }
            if(update != null){
                update.executeBatch();
                update.close();
            }
        } catch(IllegalAccessException | SQLException e){
            e.printStackTrace();
        }
    }
    
    public void addData(Saveable toSave){
        try {
            this.lastDataSaved.put(toSave, field.get(toSave));
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }
    
    public void removeData(Saveable toSave){
        Object data = lastDataSaved.remove(toSave);
        if(data == null){
            return;
        }
        try {
            Object currentData = field.get(toSave);
            if(!data.equals(currentData)){
                Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                    try {
                        PreparedStatement update = ConsulatAPI.getDatabase().prepareStatement(sqlStatement);
                        update.setObject(1, currentData);
                        lastDataSaved.replace(toSave, currentData);
                        update.setString(2, toSave.getDatabaseIdentifier());
                        update.executeUpdate();
                        update.close();
                    } catch(SQLException e){
                        e.printStackTrace();
                    }
                });
            }
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }
}
