package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerFillBucketMilkEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerFillBucketMilkEvent(Entity entity, Player player){
        super(entity, player);
    }
}
