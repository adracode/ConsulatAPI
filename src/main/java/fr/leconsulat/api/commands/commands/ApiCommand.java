package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.jetbrains.annotations.NotNull;

public class ApiCommand extends ConsulatCommand {
    public ApiCommand(){
        super(ConsulatAPI.getConsulatAPI(), "api");
        setDescription("Gérer l'API public").
                setUsage("/api - Voir l'aide sur l'API\n" +
                        "/api on - Activer l'API\n" +
                        "/api off - Désactiver l'API").
                setRank(Rank.JOUEUR).
                suggest(LiteralArgumentBuilder.literal("on"),
                        LiteralArgumentBuilder.literal("off"));
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer player, @NotNull String[] args){
        if(args.length == 0){
            player.sendMessage(Text.HELP_API(player));
            return;
        }
        switch(args[0].toLowerCase()){
            case "on":
                if(player.isApi()){
                    player.sendMessage(Text.API_ALREADY_ON);
                    return;
                }
                player.sendMessage(Text.API_ON);
                player.setApi(true);
                break;
            case "off":
                if(!player.isApi()){
                    player.sendMessage(Text.API_ALREADY_OFF);
                    return;
                }
                player.sendMessage(Text.API_OFF);
                player.setApi(false);
                break;
        }
    }
}
