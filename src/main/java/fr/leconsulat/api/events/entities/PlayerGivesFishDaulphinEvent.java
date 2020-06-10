package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerGivesFishDaulphinEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerGivesFishDaulphinEvent(Entity entity, Player player){
        super(entity, player);
    }
}
