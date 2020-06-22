package fr.leconsulat.api.commands.commands;

import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

public class GCCommand extends ConsulatCommand {
    
    public GCCommand(){
        super("gc", "/gc", 0, Rank.ADMIN);
        setPermission("consulat.api.command.gc");
    }
    
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        System.gc();
    }
}
