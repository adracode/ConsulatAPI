package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.GuiListener;
import fr.leconsulat.api.gui.events.GuiClickEvent;
import fr.leconsulat.api.gui.events.GuiCloseEvent;
import fr.leconsulat.api.gui.events.GuiCreateEvent;
import fr.leconsulat.api.gui.events.GuiOpenEvent;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;

public class TestGui extends GuiListener<ConsulatPlayer> {
    
    private ChildTestGui childTestGui = new ChildTestGui();
    
    public TestGui(){
        super(ConsulatPlayer.class, 4);
        setTemplate(this, "§cCou§ecou",
                getItem("§aSalut", 10, Material.ENDER_PEARL, "§7Ceci est un §8item §7de §cTest", "§7Veuillez le traiter avec §arespect"),
                getItem("nom du joueur", 28, Material.HEART_OF_THE_SEA),
                getItem("§aSalut", 0, Material.ENDER_PEARL, "§dOui").setGlowing(true)
        );
    }
    
    @Override
    public void onCreate(GuiCreateEvent event){
        event.getGui().setDisplayName(28, ((ConsulatPlayer)event.getKey()).getName());
    }
    
    @Override
    public void onOpen(GuiOpenEvent event){
        event.getPlayer().sendMessage("Je te vois en fait");
    }
    
    @Override
    public void onClose(GuiCloseEvent event){
        event.getPlayer().sendMessage("Eh ne reviens pas");
    }
    
    @Override
    public void onClick(GuiClickEvent event){
        event.getPlayer().sendMessage("" + event.getSlot());
        if(event.getSlot() == 10){
            childTestGui.open(event.getPlayer(), 0, event.getGui());
        }
    }
}
