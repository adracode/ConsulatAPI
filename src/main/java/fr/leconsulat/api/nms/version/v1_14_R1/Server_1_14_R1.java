package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.nms.api.Server;
import fr.leconsulat.api.nms.api.server.DedicatedServer;
import fr.leconsulat.api.nms.version.v1_14_R1.server.DedicatedServer_1_14_R1;

public class Server_1_14_R1 implements Server {
    
    private DedicatedServer_1_14_R1 dedicatedServer;
    
    @Override
    public DedicatedServer getDedicatedServer(){
        return dedicatedServer == null ? dedicatedServer = new DedicatedServer_1_14_R1() : dedicatedServer;
    }
}
