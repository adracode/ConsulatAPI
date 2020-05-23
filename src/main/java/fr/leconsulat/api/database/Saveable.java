package fr.leconsulat.api.database;

public interface Saveable {
    
    String getDatabaseIdentifier();
    
    int hashCode();
    
    boolean equals(Object that);
    
}
