package com.ltq.rpc.test;

import com.ltq.rpc.config.Config;
import com.ltq.rpc.socket.ClientProxy;

public class ClientTest {
    public static void main(String[] args) {
    	System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true");
    	ClientProxy cp=new ClientProxy(Config.HOST_ADDRESS, Config.PORT);   
    	
    	//代理对象生成==>实现了Hello 接口方法==>invoke
    	//代理对象调用
        Hello hello= (Hello)cp.getClientIntance(Hello.class);
        System.out.println( hello.add(1,2));
        
        Counter counter=(Counter) cp.getClientIntance(Counter.class);
        for(int i=0;i<10;i++){        	
        	System.out.println(counter.count());
        }
    }
    
}
