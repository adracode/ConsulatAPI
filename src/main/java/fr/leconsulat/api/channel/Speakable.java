package fr.leconsulat.api.channel;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Speakable {
    
    @NotNull String speak(@NotNull ConsulatPlayer player, @NotNull String message);
    
}
