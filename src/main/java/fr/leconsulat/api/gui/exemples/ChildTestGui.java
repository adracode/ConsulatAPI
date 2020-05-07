package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.GuiListener;
import fr.leconsulat.api.gui.events.GuiClickEvent;
import fr.leconsulat.api.gui.events.GuiCloseEvent;
import fr.leconsulat.api.gui.events.GuiCreateEvent;
import fr.leconsulat.api.gui.events.GuiOpenEvent;
import org.bukkit.Material;

public class ChildTestGui extends GuiListener {
    
    public ChildTestGui(GuiListener father){
        super(father, int.class);
        addGui(null, this, "Classement", 6,
                getItem("1er", 0, "adracode", "§r§aBravo !", "§7Tu es le premier !"),
                getItem("Page: ", 49, Material.PAPER).setGlowing(true),
                getItem("Suivant", 53, Material.ARROW),
                getItem("Précédent", 45, Material.ARROW),
                getItem("§eJe me déplace !", 1, Material.SLIME_BALL)
        );
        setCreateOnOpen(true);
    }
    
    @Override
    public void onCreate(GuiCreateEvent event){
        if(event.getKey() == null){
            return;
        }
        event.getGui().setDisplayName(49, "Page: " + event.getKey());
        int slot = ((int)event.getKey()) % 45;
        if(slot == 0){
            event.getGui().setDisplayName(0, "§62e");
            event.getGui().setDescription(0, "§6Tu es passé 2e :(");
        }
        event.getGui().moveItem(1, slot);
        
    }
    
    @Override
    public void onOpen(GuiOpenEvent event){
    
    }
    
    @Override
    public void onClose(GuiCloseEvent event){
        event.setFatherKey(event.getPlayer());
    }
    
    @Override
    public void onClick(GuiClickEvent event){
        switch(event.getSlot()){
            case 53:{
                open(event.getPlayer(), (int)event.getGui().getKey() + 1);
            }
            break;
            case 45:
                getGui((int)event.getGui().getKey() - 1).open(event.getPlayer());
                break;
        }
    }
}
