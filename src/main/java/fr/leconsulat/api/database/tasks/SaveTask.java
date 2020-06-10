package fr.leconsulat.api.database.tasks;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.database.FillStatement;
import fr.leconsulat.api.database.Saveable;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SaveTask<T extends Saveable, D> implements Runnable {
    
    private String sqlStatement;
    private Map<T, D> lastKnownsData = new HashMap<>();
    private FillStatement<T> fillUpdate;
    private Function<T, D> getData;

    public SaveTask(String sql, FillStatement<T> fillUpdate, Function<T, D> getData){
        this.sqlStatement = sql;
        this.fillUpdate = fillUpdate;
        this.getData = getData;
    }
    
    @Override
    public void run(){
        try {
            PreparedStatement statement = null;
            for(Map.Entry<T, D> saveableData : lastKnownsData.entrySet()){
                T saveable = saveableData.getKey();
                D knownData = saveableData.getValue();
                D currentData = getData.apply(saveable);
                if(currentData == null && knownData == null || currentData != null && currentData.equals(knownData)){
                    continue;
                }
                if(statement == null){
                    statement = ConsulatAPI.getDatabase().prepareStatement(sqlStatement);
                }
                fillUpdate.fill(statement, saveable);
                statement.addBatch();
            }
            if(statement != null){
                statement.executeBatch();
                statement.close();
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void addData(Saveable data){
        lastKnownsData.put((T)data, getData.apply((T)data));
    }
    
    public void saveOnce(Saveable toSave){
        addData(toSave);
        removeData(toSave, true, true);
    }
    
    public void removeData(Saveable toSave, boolean save){
        removeData(toSave, save, false);
    }
    
    @SuppressWarnings("unchecked")
    private void removeData(Saveable toSave, boolean save, boolean force){
        Object data = lastKnownsData.remove(toSave);
        if(save){
            Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                try {
                    Object currentData = getData.apply((T)toSave);
                    if(force || ((currentData != null || data != null) && (currentData == null || !currentData.equals(data)))){
                        PreparedStatement statement = ConsulatAPI.getDatabase().prepareStatement(sqlStatement);
                        fillUpdate.fill(statement, (T)toSave);
                        statement.executeUpdate();
                        statement.close();
                    }
                } catch(SQLException e){
                    e.printStackTrace();
                }
            });
        }
    }
    
    public void removeDatas(){
        run();
        lastKnownsData.clear();
    }
}
