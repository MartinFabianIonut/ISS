package ISSProject.network.utils;

import ISSProject.network.rpcprotocol.ClientRpcReflectionWorker;
import ISSProject.service.IService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;


public class RpcConcurrentServer extends AbsConcurrentServer {
    private final IService chatServer;
    private static final Logger logger= LogManager.getLogger();
    public RpcConcurrentServer(int port, IService chatServer) {
        super(port);
        this.chatServer = chatServer;
        logger.info("RpcConcurrentServer created");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcReflectionWorker worker=new ClientRpcReflectionWorker(chatServer, client);
        return new Thread(worker);
    }

    @Override
    public void stop(){
        logger.info("Stopping services ...");
    }
}
