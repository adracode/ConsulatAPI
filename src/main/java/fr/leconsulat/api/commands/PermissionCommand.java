package fr.leconsulat.api.commands;

import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;

public class PermissionCommand extends ConsulatCommand {
    
    public PermissionCommand(){
        super(
                "permission",
                "oui",
                3,
                Rank.DEVELOPPEUR
        );
    }
    
    @Override
    public void onCommand(ConsulatPlayer player, String[] args){
        ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(args[1]);
        if(target == null){
            player.sendMessage("§cJoueur non connecté");
            return;
        }
        switch(args[0]){
            case "has":
                player.sendMessage((target.hasPermission(args[2]) ? "§aIl a la permission " :
                        "§cIl n'a pas la permission ") + args[2]);
                break;
            case "add":
                if(target.hasPermission(args[2])){
                    player.sendMessage("§cIl a déjà la permission");
                    return;
                }
                target.addPermission(args[2]);
                player.sendMessage("§aPermission donnée");
                break;
            case "remove":
                if(!target.hasPermission(args[2])){
                    player.sendMessage("§cIl n'a pas la permission");
                    return;
                }
                target.removePermission(args[2]);
                player.sendMessage("§aIl n'a plus la permission");
                break;
        }
        return;
    }
}
