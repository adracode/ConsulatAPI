package fr.leconsulat.api.saver;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class Saver extends Thread {
    
    private static Saver instance;
    
    private long lastUpdate = System.currentTimeMillis();
    private Set<Runnable> saves = new HashSet<>();
    
    public Saver(){
        if(instance == null){
            instance = this;
        } else {
            throw new IllegalStateException("Saver is already instantiated");
        }
    }
    
    @Override
    public synchronized void run(){
        int timeBeforeCrash = 15_000;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ConsulatAPI.getConsulatAPI(), () -> {
            lastUpdate = System.currentTimeMillis();
        }, 1L, 1L);
        while(true){
            try {
                if(System.currentTimeMillis() - lastUpdate >= timeBeforeCrash){
                    ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Server has stopped responding, crash ? Saving...");
                    save();
                    lastUpdate = System.currentTimeMillis();
                }
                wait(50);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    private void save(){
        for(Runnable save : this.saves){
            save.run();
        }
        for(ConsulatPlayer player : CPlayerManager.getInstance().getConsulatPlayers()){
            player.save();
        }
    }
    
    public void addSave(Runnable save){
        this.saves.add(save);
    }
    
    public static Saver getInstance(){
        return instance;
    }
}
