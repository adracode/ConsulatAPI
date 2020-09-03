package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nms.api.Block;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;

public class Block_1_14_R1 implements Block {
    
    @Override
    public Object getBlockPosition(org.bukkit.block.Block block){
        return ((CraftBlock)block).getPosition();
    }
    
}
