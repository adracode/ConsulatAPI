package fr.leconsulat.api.graph;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Graph<T> implements Set<T> {
    
    private final Map<T, Vertex<T>> graph = new HashMap<>();
    
    private boolean addVertex(T value){
        return this.graph.putIfAbsent(value, new Vertex<>(value)) == null;
    }
    
    public void addNeighbours(T value, T neighbour){
        Vertex<T> vertex = graph.get(value);
        if(vertex == null){
            throw new NullPointerException("Vertex '" + value + "' is not in the graph.");
        }
        Vertex<T> vertexNeighbour = graph.get(neighbour);
        vertex.addNeighbour(vertexNeighbour);
        vertexNeighbour.addNeighbour(vertex);
    }
    
    public boolean removeVertex(T value){
        Vertex<T> vertex = graph.remove(value);
        if(vertex == null){
            return false;
        }
        for(Vertex<T> neigh : vertex.getNeighbours()){
            neigh.removeNeighbor(vertex);
        }
        return true;
    }
    
    public int size(){
        return graph.size();
    }
    
    public boolean isEmpty(){
        return graph.isEmpty();
    }
    
    @Override
    public boolean contains(Object o){
        return graph.containsKey(o);
    }
    
    @NotNull
    @Override
    public Iterator<T> iterator(){
        return graph.keySet().iterator();
    }
    
    @NotNull
    @Override
    public Object[] toArray(){
        return graph.keySet().toArray();
    }
    
    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a){
        return graph.keySet().toArray(a);
    }
    
    @Override
    public boolean add(T t){
        return addVertex(t);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o){
        return removeVertex((T)o);
    }
    
    @Override
    public boolean containsAll(@NotNull Collection<?> c){
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(@NotNull Collection<? extends T> c){
        boolean result = true;
        for(T e : c){
            result &= add(e);
        }
        return result;
    }
    
    @Override
    public boolean retainAll(@NotNull Collection<?> c){
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(@NotNull Collection<?> c){
        boolean result = true;
        for(Object e : c){
            result &= remove(e);
        }
        return result;
    }
    
    @Override
    public void clear(){
        graph.clear();
    }
    
    public boolean isConnected(T... withoutVertices){
        if(graph.isEmpty()){
            return true;
        }
        Set<Vertex<T>> without = new HashSet<>(withoutVertices.length);
        HashSet<Vertex<T>> done = new HashSet<>();
        for(T t : withoutVertices){
            without.add(graph.get(t));
        }
        Iterator<Vertex<T>> iterator = graph.values().iterator();
        Vertex<T> start = iterator.next();
        while(iterator.hasNext() && without.contains(start)){
            start = iterator.next();
        }
        if(without.contains(start)){
            return true;
        }
        Queue<Vertex<T>> fifo = new ArrayDeque<>(Collections.singleton(start));
        done.add(start);
        while(!fifo.isEmpty()){
            Vertex<T> v = fifo.poll();
            while(without.contains(v)){
                v = fifo.poll();
            }
            if(v == null){
                break;
            }
            for(Vertex<T> neigh : v.getNeighbours()){
                if(!done.contains(neigh) && !without.contains(neigh)){
                    done.add(neigh);
                    fifo.offer(neigh);
                }
            }
        }
        return done.size() == graph.keySet().size() - withoutVertices.length;
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<T, Vertex<T>> vertex : graph.entrySet()){
            builder.append(vertex.getKey()).append(": ");
            for(Vertex<T> neighbour : vertex.getValue().getNeighbours()){
                builder.append(neighbour).append(", ");
            }
            builder.delete(builder.length() - 2, builder.length()).append('\n');
        }
        return builder.toString();
    }
}
