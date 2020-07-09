package fr.leconsulat.api.nbt;

public final class EndTag implements Tag {
  
    public static final EndTag TAG = new EndTag();
    
    private EndTag(){
    }
    
    @Override
    public Object getValue(){
        return null;
    }
    
}
