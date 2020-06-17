package fr.leconsulat.api.commands;

import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

public class GCCommand extends ConsulatCommand {
    
    public GCCommand(){
        super("gc", "/gc", 0, Rank.DEVELOPPEUR);
    }
    
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        System.gc();
    }
}
