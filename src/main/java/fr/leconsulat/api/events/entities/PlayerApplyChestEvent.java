package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerApplyChestEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerApplyChestEvent(Entity entity, Player player){
        super(entity, player);
    }
}
