package rpc;

/**
 * @Author thesong
 * @Date 2020/11/5 12:45
 * @Version 1.0
 * @Describe
 */
public class ServerStarter {
    public static void main(String[] args) throws Exception {
        RPCServer rpcServer = new RPCServer();
        rpcServer.publish("rpc.service");
        rpcServer.start();
    }
}
