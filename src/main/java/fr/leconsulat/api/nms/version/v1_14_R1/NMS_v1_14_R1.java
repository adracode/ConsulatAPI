package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nms.api.*;

public class NMS_v1_14_R1 implements NMS {
    
    private Packet packetNMS = new Packet_1_14_R1();
    private Item itemNMS = new Item_1_14_R1();
    private Command commandNMS = new Command_1_14_R1();
    private Server serverNMS = new Server_1_14_R1();
    private NBT nbtNMS = new NBT_1_14_R1();
    private Block blockNMS = new Block_1_14_R1();
    
    @Override
    public Packet getPacket(){
        return packetNMS;
    }
    
    @Override
    public Item getItem(){
        return itemNMS;
    }
    
    public Command getCommand(){
        return commandNMS;
    }
    
    @Override
    public Server getServer(){
        return serverNMS;
    }
    
    @Override
    public NBT getNBT(){
        return nbtNMS;
    }
    
    @Override
    public Block getBlock(){
        return blockNMS;
    }
}
