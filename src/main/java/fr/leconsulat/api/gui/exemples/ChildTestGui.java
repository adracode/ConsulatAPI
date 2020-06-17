package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.gui.GuiListener;
import fr.leconsulat.api.gui.PagedGui;
import fr.leconsulat.api.gui.events.GuiClickEvent;
import fr.leconsulat.api.gui.events.PagedGuiCreateEvent;
import org.bukkit.Material;

public class ChildTestGui extends GuiListener<Integer> {
    
    public ChildTestGui(){
        super(6);
        setTemplate("Classement",
                getItem("1er", 0, "adracode", "§r§aBravo !", "§7Tu es le premier !"),
                getItem("Page: ", 49, Material.PAPER).setGlowing(true),
                getItem("Suivant", 53, Material.ARROW),
                getItem("Précédent", 45, Material.ARROW),
                getItem("§eJe me déplace !", 1, Material.SLIME_BALL)
        );
    }
    
    @Override
    public void onPageCreate(PagedGuiCreateEvent<Integer> event){
        PagedGui<Integer> pagedGui = event.getPagedGui();
        pagedGui.setDisplayName(49, "Page: " + event.getData());
        int slot = event.getData() % 45;
        if(slot == 0){
            pagedGui.setDisplayName(0, "§62e");
            pagedGui.setDescription(0, "§6Tu es passé 2e :(");
        }
        pagedGui.moveItem(1, slot);
    }
    
    @Override
    public void onClick(GuiClickEvent<Integer> event){
        switch(event.getSlot()){
            case 53:{
                Gui<Integer> gui = event.getGui().getSimilar(event.getData() + 1);
                gui.open(event.getPlayer());
            }
            break;
            case 45:
                Gui<Integer> gui = event.getGui().getSimilar(event.getData() - 1);
                gui.open(event.getPlayer());
                break;
        }
    }
}
