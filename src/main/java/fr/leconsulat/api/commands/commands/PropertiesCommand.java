package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.nms.api.server.DedicatedServer;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.jetbrains.annotations.NotNull;

public class PropertiesCommand extends ConsulatCommand {
    
    private final DedicatedServer server;
    
    public PropertiesCommand(){
        super(ConsulatAPI.getConsulatAPI(), "bukkit-properties");
        setDescription("Modifie des propriétés de Paper").
                setUsage("/properties end [<valeur>] - Modifier l'accès à l'end").
                setArgsMin(1).
                setRank(Rank.ADMIN).
                suggest(LiteralArgumentBuilder.literal("end").
                        then(RequiredArgumentBuilder.argument("valeur", BoolArgumentType.bool()))
                );
        server = ConsulatAPI.getNMS().getServer().getDedicatedServer();
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer player, @NotNull String[] args){
        switch(args[0]){
            case "end":
                if(args.length >= 2){
                    server.setProperties("settings.allow-end", Boolean.parseBoolean(args[1]));
                }
                player.sendMessage("§7End: §a" + server.getProperties("settings.allow-end"));
                break;
        }
    }
}
