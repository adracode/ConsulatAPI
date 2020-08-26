package fr.leconsulat.api.nms.api;

public interface NMS {
    
    Packet getPacket();
    
    Item getItem();
    
    Command getCommand();
    
    Server getServer();
    
    NBT getNBT();
    
    Block getBlock();
    
    Player getPlayer();
}
