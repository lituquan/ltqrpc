package com.ltq.rpc.socket;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class RpcServerStater{
    private int port;

    public RpcServerStater(int port){
        this.port=port;
    }


    public void start() {
        //启动服务
        try {
            ServerSocket serverSocket = new ServerSocket();
            SocketAddress endpoint = new InetSocketAddress(port);
            serverSocket.bind(endpoint);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new RpcHandler(socket)).start();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block 
            e.printStackTrace();
        }
    }
    
}