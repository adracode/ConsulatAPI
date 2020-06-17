package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.GuiContainer;
import fr.leconsulat.api.gui.PagedGui;
import fr.leconsulat.api.gui.events.GuiClickEvent;
import fr.leconsulat.api.gui.events.GuiCloseEvent;
import fr.leconsulat.api.gui.events.GuiOpenEvent;
import fr.leconsulat.api.gui.events.PagedGuiCreateEvent;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Material;

public class TestGui extends GuiContainer<ConsulatPlayer> {
    
    private ChildTestGui childTestGui = new ChildTestGui();
    
    public TestGui(){
        super(4);
        setTemplate("§cCou§ecou",
                getItem("§aSalut", 10, Material.ENDER_PEARL, "§7Ceci est un §8item §7de §cTest", "§7Veuillez le traiter avec §arespect"),
                getItem("nom du joueur", 28, Material.HEART_OF_THE_SEA),
                getItem("§aSalut", 0, Material.ENDER_PEARL, "§dOui").setGlowing(true)
        );
    }
    
    @Override
    public void onPageCreate(PagedGuiCreateEvent<ConsulatPlayer> event){
        PagedGui<ConsulatPlayer> playerPagedGui = event.getPagedGui();
        playerPagedGui.setDisplayName(28, event.getData().getName());
        playerPagedGui.getGui().prepareChild(0, () -> childTestGui.createGui(0, event.getGui()));
    }
    
    @Override
    public void onOpen(GuiOpenEvent<ConsulatPlayer> event){
        event.getPlayer().sendMessage("Je te vois en fait");
    }
    
    @Override
    public void onClose(GuiCloseEvent<ConsulatPlayer> event){
        event.getPlayer().sendMessage("Eh ne reviens pas");
    }
    
    @Override
    public void onClick(GuiClickEvent<ConsulatPlayer> event){
        event.getPlayer().sendMessage("" + event.getSlot());
        if(event.getSlot() == 10){
            event.getGui().getChild(0).open(event.getPlayer());
        }
    }
}
