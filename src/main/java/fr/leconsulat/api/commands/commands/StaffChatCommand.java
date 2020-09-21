package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.channel.ChannelManager;
import fr.leconsulat.api.channel.StaffChannel;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public class StaffChatCommand extends ConsulatCommand {
    
    public StaffChatCommand(){
        super(ConsulatAPI.getConsulatAPI(), "staffchat");
        setDescription("Envoyer un message dans le chat de staff").
                setUsage("/staffchat <message> - Envoyer un message").
                setAliases("sc").
                setArgsMin(1).
                setRank(Rank.BUILDER).
                suggest(RequiredArgumentBuilder.argument("message", StringArgumentType.greedyString()));
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        StaffChannel channel = (StaffChannel)ChannelManager.getInstance().getChannel("staff");
        channel.sendMessage(channel.speak(sender, StringUtils.join(args, ' ')));
    }
}
