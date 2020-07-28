package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nms.api.Inventory;
import fr.leconsulat.api.nms.api.NMS;
import fr.leconsulat.api.nms.api.Packet;

public class NMS_v1_14_R1 implements NMS {
    
    private Packet packetNMS = new Packet_1_14_R1();
    private Inventory inventoryNMS = new Inventory_1_14_R1();
    
    public NMS_v1_14_R1(){
    }
    
    @Override
    public Packet getPacketNMS(){
        return packetNMS;
    }
    
    @Override
    public Inventory getInventoryNMS(){
        return inventoryNMS;
    }
}
