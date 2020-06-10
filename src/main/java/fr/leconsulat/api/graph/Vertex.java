package fr.leconsulat.api.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Vertex<T> {
    
    private Set<Vertex<T>> neighbours = new HashSet<>();
    private T value;
    
    public Vertex(T value){
        this.value = value;
    }
    
    public boolean addNeighbour(Vertex<T> vertex){
        return neighbours.add(vertex);
    }
    
    public boolean removeNeighbor(Vertex<T> vertex){
        return neighbours.remove(vertex);
    }
    
    public Set<Vertex<T>> getNeighbours(){
        return Collections.unmodifiableSet(neighbours);
    }
    
    public T getValue(){
        return value;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>)o;
        return Objects.equals(value, vertex.value);
    }
    
    @Override
    public int hashCode(){
        return Objects.hashCode(value);
    }
    
    @Override
    public String toString(){
        return value.toString();
    }
}
