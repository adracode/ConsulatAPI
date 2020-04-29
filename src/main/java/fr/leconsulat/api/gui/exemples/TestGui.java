package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.*;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;

public class TestGui extends AGListener {
    
    public TestGui(){
        super(null, ConsulatPlayer.class);
        addGui(null,
                this, "§cCou§ecou", 4,
                addItem("§aSalut", 10, Material.ENDER_PEARL, "§7Ceci est un §8item §7de §cTest", "§7Veuillez le traiter avec §arespect"),
                addItem("nom du joueur", 28, Material.HEART_OF_THE_SEA),
                addItem("§aSalut", 0, Material.ENDER_PEARL, "§dOui").setGlowing(true)
        );
        create();
        addChild(10, new ChildTestGui(this));
    }
    
    @Override
    public void onCreate(AGCreateEvent event){
        if(event.getKey() == null){
            return;
        }
        event.getGui().setDisplayName(28, ((ConsulatPlayer)event.getKey()).getName());
    }
    
    @Override
    public void onOpen(AGOpenEvent event){
        event.getPlayer().sendMessage("Je te vois en fait");
    }
    
    @Override
    public void onClose(AGCloseEvent event){
        event.getPlayer().sendMessage("Eh ne reviens pas");
    }
    
    @Override
    public void onClick(AGClickEvent event){
        event.getPlayer().sendMessage("" + event.getSlot());
        if(event.getSlot() == 10){
            getChild(event.getSlot()).open(event.getPlayer(), 1);
        }
    }
}
