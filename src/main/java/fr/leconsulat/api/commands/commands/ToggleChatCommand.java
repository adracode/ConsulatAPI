package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.redis.RedisManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RTopic;

public class ToggleChatCommand extends ConsulatCommand {
    
    private RTopic toggleChat = RedisManager.getInstance().getRedis().getTopic("ToggleChat");
    
    public ToggleChatCommand(){
        super(ConsulatAPI.getConsulatAPI(), "togglechat");
        setDescription("Activer ou désactiver le chat").
                setUsage("/chat - Switcher le chat").
                setAliases("chat").
                setRank(Rank.RESPONSABLE).
                suggest();
        RedisManager.getInstance().register("ToggleChat", String.class, (channel, player) -> {
            ConsulatAPI core = ConsulatAPI.getConsulatAPI();
            core.setChat(!core.isChat());
            if(core.isChat()){
                Bukkit.broadcastMessage(Text.BRODCAST(player, "Le chat est à nouveau disponible."));
            } else {
                Bukkit.broadcastMessage(Text.BRODCAST(player, "Le chat est coupé."));
            }
        });
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        toggleChat.publishAsync(sender.getName());
    }
}
