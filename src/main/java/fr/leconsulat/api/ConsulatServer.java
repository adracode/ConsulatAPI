package fr.leconsulat.api;

public enum ConsulatServer {
    
    HUB("Hub"),
    SURVIE("Survie"),
    SAFARI("Safari"),
    UNKNOWN("?");
    
    private final String display;
    
    ConsulatServer(String display){
        this.display = display;
    }
    
    public String getDisplay(){
        return display;
    }
}
