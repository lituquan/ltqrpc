package com.ltq.rpc.test;

import com.ltq.rpc.config.Config;
import com.ltq.rpc.register.ServiceContainer;
import com.ltq.rpc.socket.RpcHandler;
import com.ltq.rpc.socket.RpcServerStater;


//客户端request==>报文==>socket[i,o]==>报文==>服务端执行请求==>result==>报文
//==>socket[i,o]==>报文==>客户端反序列化
public class ServerTest {
    public static void main(String[] args) {
        //注册服务1
    	ServiceContainer.register(Hello.class, HelloImpl.class);
        //注册服务2
    	ServiceContainer.register(Counter.class, CounterImpl.class);
        //启动服务
        new RpcServerStater(Config.PORT).start();
        
    }
}