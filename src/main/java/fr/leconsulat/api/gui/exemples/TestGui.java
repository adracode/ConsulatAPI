package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Relationnable;
import fr.leconsulat.api.gui.gui.template.DataRelatGui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class TestGui extends DataRelatGui<ConsulatPlayer> {
    
    public TestGui(ConsulatPlayer player){
        super(player, "§cCoucou", 4,
                IGui.getItem("§aSalut", 10, Material.ENDER_PEARL, "§7Ceci est un §8item §7de §cTest", "§7Veuillez le traiter avec §arespect"),
                IGui.getItem("nom du joueur", 28, Material.HEART_OF_THE_SEA),
                IGui.getItem("§aSalut", 0, Material.ENDER_PEARL, "§dOui").setGlowing(true)
        );
        setDisplayName(28, player.getName());
    }
    
    @Override
    public void onOpened(GuiOpenEvent event){
        event.getPlayer().sendMessage("Je te vois en fait");
        setDescriptionPlayer(28, event.getPlayer(), "Oui");
    }
    
    @Override
    public void onClose(GuiCloseEvent event){
        event.getPlayer().sendMessage("Eh ne reviens pas");
    }
    
    @Override
    public void onClick(GuiClickEvent event){
        event.getPlayer().sendMessage("" + event.getSlot());
        if(event.getSlot() == 10){
            getChild(0).open(event.getPlayer());
        }
    }
    
    @Override
    public Relationnable createChild(@Nullable Object key){
        return new ChildTestGui();
    }
}
