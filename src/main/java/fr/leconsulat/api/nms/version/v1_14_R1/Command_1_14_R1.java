package fr.leconsulat.api.nms.version.v1_14_R1;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.leconsulat.api.nms.api.Command;
import net.minecraft.server.v1_14_R1.CommandListenerWrapper;
import org.bukkit.entity.Player;

public class Command_1_14_R1 implements Command {
    
    public Player getPlayerFromListener(Object listener){
        if(!(listener instanceof CommandListenerWrapper)){
            return null;
        }
        CommandListenerWrapper commandListener = (CommandListenerWrapper)listener;
        try {
            return commandListener.h().getBukkitEntity();
        } catch(CommandSyntaxException e){
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Player getPlayerFromContextSource(Object source){
        CommandListenerWrapper commandListenerWrapper = (CommandListenerWrapper)source;
        if(commandListenerWrapper.getEntity() == null){
            return null;
        }
        return (Player)commandListenerWrapper.getEntity().getBukkitEntity();
    }
    
}
