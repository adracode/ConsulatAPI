package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.events.ChatSyncEvent;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.redis.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RTopic;

public class ToggleChatCommand extends ConsulatCommand implements Listener {
    
    private RTopic toggleChat;
    private int toggleChatListener;
    
    public ToggleChatCommand(){
        super(ConsulatAPI.getConsulatAPI(), "togglechat");
        setDescription("Activer ou désactiver le chat").
                setUsage("/chat - Switcher le chat").
                setAliases("chat").
                setRank(Rank.RESPONSABLE).
                suggest();
        Bukkit.getServer().getPluginManager().registerEvents(this, ConsulatAPI.getConsulatAPI());
    }
    
    @EventHandler
    public void onSyncChat(ChatSyncEvent event){
        String topicName = "ToggleChat" + (ConsulatAPI.getConsulatAPI().isDevelopment() ? "Dev" : "");
        if(event.isSync()){
            toggleChatListener = RedisManager.getInstance().register(topicName, String.class, (channel, player) -> {
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
        } else {
            RedisManager.getInstance().getRedis().getTopic(topicName).removeListener(toggleChatListener);
            toggleChat = null;
        }
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        if(toggleChat != null){
            toggleChat.publishAsync(!ConsulatAPI.getConsulatAPI().isChat() + ":" + sender.getName());
        } else {
            ConsulatAPI core = ConsulatAPI.getConsulatAPI();
            core.setChat(!core.isChat());
            if(core.isChat()){
                Bukkit.broadcastMessage(Text.BRODCAST(sender.getName(), "Le chat est à nouveau disponible."));
            } else {
                Bukkit.broadcastMessage(Text.BRODCAST(sender.getName(), "Le chat est coupé."));
            }
        }
    }
}
