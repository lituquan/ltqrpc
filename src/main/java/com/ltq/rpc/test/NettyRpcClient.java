package com.ltq.rpc.test;

import com.ltq.rpc.config.Config;
import com.ltq.rpc.netty.NettyClientProxy;
import com.ltq.rpc.register.ServiceRegistry;
import com.ltq.rpc.register.zookeeper.ZooKeeperServiceDiscovery;
/*
*
* 组合spring:
* 	注册中心可以在spring中加载,代理类可以直接在spring中生成
* 对应dubbo 中的：
* 	提供注册中心地址,@Reference可以根据接口名获取代理
* 
* 服务更新和服务下线通知
* 服务降级
* 服务限流
*/
public class NettyRpcClient {
	public static void main(String[] args) throws InterruptedException {
		//连接注册中心
		String zkAddress="127.0.0.1:2181";
		ServiceRegistry serviceRegistry0=new ZooKeeperServiceDiscovery(zkAddress);
		//服务代理对象
		NettyClientProxy proxy=new NettyClientProxy(serviceRegistry0);
		Hello clientIntance = (Hello) proxy.getClientIntance(Hello.class);

		Counter counter= (Counter) proxy.getClientIntance(Counter.class);
		//服务调用
		System.out.println(clientIntance.add(100, 50));
		System.out.println(clientIntance.add(100, 50));
		System.out.println(clientIntance.add(100, 50));

		for (int i = 0; i < 100; i++) {
			System.out.println(counter.count());
		}
	}
}
