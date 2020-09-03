package fr.leconsulat.api.nbt;

public interface NumberTag extends Tag {
    
    long getLong();
    
    int getInt();
    
    short getShort();
    
    byte getByte();
    
    double getDouble();
    
    float getFloat();
    
    Number getAsNumber();
    
}
