package fr.leconsulat.api.server;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerQueue implements Iterable<ConsulatPlayer> {
    
    private final List<ConsulatPlayer> queue = new ArrayList<>();
    
    public boolean addPlayer(ConsulatPlayer player){
        return queue.add(player);
    }
    
    public ConsulatPlayer pickPlayer(){
        if(queue.isEmpty()){
            return null;
        }
        return queue.remove(0);
    }
    
    public void removePlayer(ConsulatPlayer player){
        queue.remove(player);
    }
    
    public ConsulatPlayer last(){
        return queue.isEmpty() ? null : queue.get(queue.size() - 1);
    }
    
    public boolean isEmpty(){
        return queue.isEmpty();
    }
    
    public int size(){
        return queue.size();
    }
    
    @Override
    public @NotNull Iterator<ConsulatPlayer> iterator(){
        return new PlayerIterator();
    }
    
    private class PlayerIterator implements Iterator<ConsulatPlayer> {
    
        private int index = 0;
        
        @Override
        public boolean hasNext(){
            return index < size();
        }
    
        @Override
        public ConsulatPlayer next(){
            return queue.get(index++);
        }
    }
    
}
