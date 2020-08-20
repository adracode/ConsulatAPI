package fr.leconsulat.api.database.tasks;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.database.FillStatement;
import fr.leconsulat.api.database.Saveable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class SaveTask<T extends Saveable, D> implements Runnable {
    
    private final @NotNull String sqlStatement;
    private final @NotNull FillStatement<T> fillUpdate;
    private final @NotNull Function<T, D> getData;
    private final @NotNull Map<T, D> lastKnownsData = new HashMap<>();
    
    public SaveTask(@NotNull String sql, @NotNull FillStatement<T> fillUpdate, @NotNull Function<T, D> getData){
        this.sqlStatement = Objects.requireNonNull(sql, "sql");
        this.fillUpdate = Objects.requireNonNull(fillUpdate, "fillUpdate");
        this.getData = Objects.requireNonNull(getData, "getData");
    }
    
    @SuppressWarnings("unchecked")
    public void addData(@NotNull Saveable saveable){
        Objects.requireNonNull(saveable, "saveable");
        lastKnownsData.put((T)saveable, getData.apply((T)saveable));
    }
    
    public void removeData(@NotNull Saveable toSave, boolean save){
        removeData(toSave, save, false);
    }
    
    public void removeDatas(){
        run();
        lastKnownsData.clear();
    }
    
    public void saveOnce(Saveable toSave){
        addData(toSave);
        removeData(toSave, true, true);
    }
    
    @SuppressWarnings("unchecked")
    private void removeData(@NotNull Saveable toSave, boolean save, boolean force){
        T objectToSave = (T)toSave;
        Objects.requireNonNull(objectToSave, "objectToSave");
        Object data = lastKnownsData.remove(objectToSave);
        if(save){
            if(!ConsulatAPI.getConsulatAPI().isEnabled()){
                try {
                    Object currentData = getData.apply(objectToSave);
                    if(force || ((currentData != null || data != null) && (currentData == null || !currentData.equals(data)))){
                        PreparedStatement statement = ConsulatAPI.getDatabase().prepareStatement(sqlStatement);
                        fillUpdate.fill(statement, objectToSave);
                        statement.executeUpdate();
                        statement.close();
                    }
                } catch(SQLException e){
                    e.printStackTrace();
                }
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
                    try {
                        Object currentData = getData.apply(objectToSave);
                        if(force || ((currentData != null || data != null) && (currentData == null || !currentData.equals(data)))){
                            PreparedStatement statement = ConsulatAPI.getDatabase().prepareStatement(sqlStatement);
                            fillUpdate.fill(statement, objectToSave);
                            statement.executeUpdate();
                            statement.close();
                        }
                    } catch(SQLException e){
                        e.printStackTrace();
                    }
                });
            }
        }
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
                lastKnownsData.put(saveable, currentData);
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
}
