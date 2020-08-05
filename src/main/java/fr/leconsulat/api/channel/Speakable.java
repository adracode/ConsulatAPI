package fr.leconsulat.api.channel;

import fr.leconsulat.api.player.ConsulatPlayer;

public interface Speakable {
    
    String speak(ConsulatPlayer player, String message);
    
}
