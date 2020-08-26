package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nms.api.Player;
import net.minecraft.server.v1_14_R1.EntityArrow;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class Player_1_14_R1 implements Player {
    
    @Override
    public void pickup(org.bukkit.entity.Player player, Entity entity){
        switch(entity.getType()){
            case ARROW:
            case SPECTRAL_ARROW:
            case DROPPED_ITEM:
            case TRIDENT:
                break;
            default:
                return;
        }
        if(entity.getType() != EntityType.DROPPED_ITEM){
            EntityArrow arrowEntity = (EntityArrow)((CraftEntity)entity).getHandle();
            arrowEntity.shake = 0;
            arrowEntity.inGround = true;
            arrowEntity.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
        }
        ((CraftEntity)entity).getHandle().pickup(((CraftPlayer)player).getHandle());
    }
    
}
