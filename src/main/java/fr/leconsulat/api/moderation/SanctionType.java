package fr.leconsulat.api.moderation;

import org.bukkit.Material;

import java.io.Serializable;

public enum SanctionType implements Serializable {
    
    KICK(Material.MINECART),
    MUTE(Material.PAPER),
    BAN(Material.BARRIER);
    
    Material material;
    
    SanctionType(Material material){
        this.material = material;
    }
    
    public Material getMaterial(){
        return material;
    }
}
