package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ToggleChatCommand extends ConsulatCommand {
    
    public ToggleChatCommand(){
        super(ConsulatAPI.getConsulatAPI(), "togglechat");
        setDescription("Activer ou désactiver le chat").
                setUsage("/chat - Switcher le chat").
                setAliases("chat").
                setRank(Rank.RESPONSABLE).
                suggest();
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        ConsulatAPI core = ConsulatAPI.getConsulatAPI();
        core.setChat(!core.isChat());
        if(core.isChat()){
            Bukkit.broadcastMessage(Text.BRODCAST(sender.getName(), "Le chat est à nouveau disponible."));
        } else {
            Bukkit.broadcastMessage(Text.BRODCAST(sender.getName(), "Le chat est coupé."));
        }
    }
}
