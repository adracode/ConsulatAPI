package fr.leconsulat.api.task;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.Queue;

public class TaskManager {
    
    private static final TaskManager instance = new TaskManager();
    
    private final Queue<Task> queue = new ArrayDeque<>();
    private Task currentTask = null;
    
    private TaskManager(){
    }
    
    private void runTasks(){
        if(queue.isEmpty() && currentTask == null){
            return;
        }
        if(currentTask == null){
            currentTask = queue.poll();
            currentTask.onStart();
        }
        int timeGiven = Math.max((50 - ConsulatAPI.getConsulatAPI().getLastTimeTick() - 5), 5);
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() < start + timeGiven){
            currentTask.next();
            if(currentTask.isFinished()){
                currentTask.onFinish();
                if(queue.isEmpty()){
                    currentTask = null;
                    return;
                }
                currentTask = queue.poll();
                currentTask.onStart();
            }
        }
        if(!queue.isEmpty() || currentTask != null){
            Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), this::runTasks);
        }
    }
    
    public void addTask(Task task){
        queue.add(task);
        if(queue.size() == 1 && currentTask == null){
            runTasks();
        }
    }
    
    public static TaskManager getInstance(){
        return instance;
    }
}
