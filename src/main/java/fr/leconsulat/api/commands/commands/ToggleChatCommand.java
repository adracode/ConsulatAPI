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
    
    private RTopic toggleChat;
    
    public ToggleChatCommand(){
        super(ConsulatAPI.getConsulatAPI(), "togglechat");
        setDescription("Activer ou désactiver le chat").
                setUsage("/chat - Switcher le chat").
                setAliases("chat").
                setRank(Rank.RESPONSABLE).
                suggest();
        String topicName = "ToggleChat" + (ConsulatAPI.getConsulatAPI().isDevelopment() ? "Dev" : "");
        RedisManager.getInstance().register(topicName, String.class, (channel, player) -> {
            ConsulatAPI core = ConsulatAPI.getConsulatAPI();
            int separator = player.indexOf(':');
            boolean status = Boolean.parseBoolean(player.substring(0, separator));
            if(core.isChat() == status){
                return;
            }
            core.setChat(status);
            if(core.isChat()){
                Bukkit.broadcastMessage(Text.BRODCAST(player.substring(separator + 1), "Le chat est à nouveau disponible."));
            } else {
                Bukkit.broadcastMessage(Text.BRODCAST(player.substring(separator + 1), "Le chat est coupé."));
            }
        });
        toggleChat = RedisManager.getInstance().getRedis().getTopic(topicName);
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        toggleChat.publishAsync(sender.getName());
    }
}
