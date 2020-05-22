package com.ltq.rpc.test;

import com.ltq.rpc.config.Config;
import com.ltq.rpc.netty.NettyRpcServer;
import com.ltq.rpc.netty.RpcServerHandler;
import com.ltq.rpc.register.ServiceRegistry;
import com.ltq.rpc.register.zookeeper.ZooKeeperServiceDiscovery;


/*
 * 组合spring:
 *  提供注册中心地址 zkAddress , 
 *  标记要发布的对象@Service,
 *  提供部署端口
 *  
 * 服务下线：
 * 	故障移除
 */

//客户端request==>报文==>socket[i,o]==>报文==>服务端请求方法==>result==>报文
//==>socket[i,o]==>报文==>客户端对象
public class NettyServerTest {
    public static void main(String[] args) {
    	//连接注册中心
    	String zkAddress="127.0.0.1:2181";
		ServiceRegistry serviceRegistry0=new ZooKeeperServiceDiscovery(zkAddress);
		RpcServerHandler.setServiceRegistry(serviceRegistry0);
        //注册服务1
		String ip="127.0.0.1";
    	RpcServerHandler.register(Hello.class, HelloImpl.class,ip+":"+Config.PORT);
        //注册服务2
    	RpcServerHandler.register(Counter.class, CounterImpl.class,ip+":"+Config.PORT);
        //启动服务
        new NettyRpcServer(Config.PORT).start();
        
    }
}