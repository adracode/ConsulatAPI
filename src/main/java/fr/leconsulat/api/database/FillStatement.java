package fr.leconsulat.api.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface FillStatement<T extends Saveable> {
    
    void fill(PreparedStatement statement, T toSave) throws SQLException;
    
}
