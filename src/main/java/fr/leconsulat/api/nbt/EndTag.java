package fr.leconsulat.api.nbt;

public final class EndTag implements Tag {
  
    public static final EndTag TAG = new EndTag();
    
    private static final long serialVersionUID = -3849379622044400505L;
    
    private EndTag(){
    }
    
    @Override
    public Object getValue(){
        return null;
    }
    
    @Override
    public NBTType getType(){
        return NBTType.END;
    }
    
    @Override
    public String toString(){
        return "EndTag";
    }
    
    @Override
    public boolean equals(Object obj){
        return obj.getClass() == this.getClass();
    }
    
    @Override
    public int hashCode(){
        return -1;
    }
}
