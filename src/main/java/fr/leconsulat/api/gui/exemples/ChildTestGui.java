package fr.leconsulat.api.gui.exemples;

import fr.leconsulat.api.gui.*;
import org.bukkit.Material;

public class ChildTestGui extends AGListener {
    
    public ChildTestGui(AGListener father){
        super(father, int.class);
        addGui(null, this, "Classement", 6,
                addItem("1er", 0, "adracode", "§r§aBravo !", "§7Tu es le premier !"),
                addItem("Page: ", 49, Material.PAPER).setGlowing(true),
                addItem("Suivant", 53, Material.ARROW),
                addItem("Précédent", 45, Material.ARROW),
                addItem("§eJe me déplace !", 1, Material.SLIME_BALL)
        );
        create();
        setCreateOnOpen(true);
    }
    
    @Override
    public void onCreate(AGCreateEvent event){
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
    public void onOpen(AGOpenEvent event){
    
    }
    
    @Override
    public void onClose(AGCloseEvent event){
        event.setKey(event.getPlayer());
    }
    
    @Override
    public void onClick(AGClickEvent event){
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
