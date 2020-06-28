package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCreateEvent;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.gui.gui.template.DataRelatPagedGui;
import org.bukkit.Material;

public class ChildTestGui extends DataRelatPagedGui<Void> {
    
    public ChildTestGui(){
        super(null, "Classement", 6,
                IGui.getItem("1er", 0, "adracode", "§r§aBravo !", "§7Tu es le premier !"),
                IGui.getItem("Page: ", 49, Material.PAPER).setGlowing(true),
                IGui.getItem("Suivant", 53, Material.ARROW),
                IGui.getItem("Précédent", 45, Material.ARROW),
                IGui.getItem("§eJe me déplace !", 1, Material.SLIME_BALL)
        );
    }
    
    @Override
    public void onPageCreated(GuiCreateEvent event, Pageable pageGui){
        pageGui.setName("Classement " + pageGui.getPage());
        pageGui.setDisplayName(49, "Page: " + pageGui.getPage());
        int slot = pageGui.getPage() % 45;
        if(slot == 0){
            pageGui.setDisplayName(0, "§62e");
            pageGui.setDescription(0, "§6Tu es passé 2e :(");
        }
        pageGui.moveItem(1, slot);
    }
    
    @Override
    public void onPageClick(GuiClickEvent event, Pageable pageable){
        switch(event.getSlot()){
            case 53:{
                getPage(pageable.getPage() + 1).open(event.getPlayer());
            }
            break;
            case 45:
                getPage(pageable.getPage() - 1).open(event.getPlayer());
                break;
        }
        
    }
    
    
}
