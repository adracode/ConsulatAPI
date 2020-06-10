package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerHealZombieVillagerEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerHealZombieVillagerEvent(Entity entity, Player player){
        super(entity, player);
    }
}
