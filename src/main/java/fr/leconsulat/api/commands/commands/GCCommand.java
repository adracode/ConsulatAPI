package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.jetbrains.annotations.NotNull;

public class GCCommand extends ConsulatCommand {
    
    public GCCommand(){
        super(ConsulatAPI.getConsulatAPI(),"gc");
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer player, @NotNull String[] args){
        System.gc();
    }
}
