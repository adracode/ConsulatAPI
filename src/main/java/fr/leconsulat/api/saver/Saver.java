package fr.leconsulat.api.saver;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nms.api.server.DedicatedServer;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class Saver extends Thread {
    
    private static Saver instance;
    
    private boolean stop = false;
    private long lastUpdate = Long.MAX_VALUE;
    private Set<Runnable> saves = new HashSet<>();
    private DedicatedServer server = ConsulatAPI.getNMS().getServer().getDedicatedServer();
    
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
        ConsulatAPI.getConsulatAPI().log(Level.INFO, "Starting saver.");
        while(!server.isStopped() && !stop){
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
        ConsulatAPI.getConsulatAPI().log(Level.INFO, "Server is stopping, saver disabled.");
    }
    
    public void end(){
        stop = true;
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
