package fr.leconsulat.api.nms.api;

import org.bukkit.entity.Player;

public interface Command {
    
    Player getPlayerFromListener(Object listener);
    
    Player getPlayerFromContextSource(Object listener);
    
}
