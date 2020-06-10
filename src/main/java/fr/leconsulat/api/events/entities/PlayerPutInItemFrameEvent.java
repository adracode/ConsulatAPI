package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerPutInItemFrameEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerPutInItemFrameEvent(Entity entity, Player player){
        super(entity, player);
    }
}
