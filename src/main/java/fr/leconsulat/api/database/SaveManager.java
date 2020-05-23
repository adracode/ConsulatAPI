package fr.leconsulat.api.database;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class SaveManager {
    
    private static SaveManager instance;
    
    private Map<String, SaveTask> tasks = new HashMap<>();
    
    public SaveManager(){
        if(instance != null){
            return;
        }
        instance = this;
        //int delay = 10 * 20;
        int delay = 5 * 60 * 20;
        Bukkit.getScheduler().runTaskTimerAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            for(SaveTask task : tasks.values()){
                task.run();
            }
        }, delay, delay);
    }
    
    public void addSaveTask(String id, SaveTask task){
        tasks.put(id, task);
    }
    
    public void addData(String id, Saveable toSave){
        SaveTask task = tasks.get(id);
        if(task != null){
            task.addData(toSave);
        }
    }
    
    public void removeData(String id, Saveable toSave){
        SaveTask task = tasks.get(id);
        if(task != null){
            task.removeData(toSave);
        }
    }
    
    public static SaveManager getInstance(){
        return instance;
    }
}
