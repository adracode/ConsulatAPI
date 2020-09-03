package fr.leconsulat.api.server;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class PlayerQueue implements Iterable<ConsulatPlayer> {
    
    private final Deque<ConsulatPlayer> queue = new LinkedList<>();
    
    public boolean addPlayer(ConsulatPlayer player){
        if(player == null){
            return false;
        }
        queue.removeIf(playerQueue -> playerQueue == null || playerQueue.equals(player));
        return queue.add(player);
    }
    
    public ConsulatPlayer pickPlayer(){
        if(queue.isEmpty()){
            return null;
        }
        ConsulatPlayer player = queue.poll();
        player.setPositionInQueue(0);
        return player;
    }
    
    public ConsulatPlayer last(){
        return queue.isEmpty() ? null : queue.peekLast();
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
    
        private Iterator<ConsulatPlayer> iterator = queue.iterator();
        
        @Override
        public boolean hasNext(){
            return iterator.hasNext();
        }
    
        @Override
        public ConsulatPlayer next(){
            return iterator.next();
        }
    }
    
}
