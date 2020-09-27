package com.ltq.rpc.netty;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.ltq.rpc.code.Request;
import com.ltq.rpc.code.Response;
import com.ltq.rpc.register.ServiceRegistry;
import com.ltq.rpc.socket.RpcFuture;


public class NettyClientProxy<T> implements InvocationHandler {
    private RpcClientHandler ch;

    public NettyClientProxy(ServiceRegistry registry) {
        this.ch = new RpcClientHandler(registry);
    }
    
	//反射的原理：生成继承proxy的子类字节码,传递一个InvocationHandler对象
    //serviceInterface 调用方法时,
    public T getClientIntance(Class<T> serviceInterface){    
        return (T) Proxy.newProxyInstance (serviceInterface.getClassLoader(),
        		new Class<?>[]{serviceInterface},
        		this);
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request req=new Request();
        req.setMessageId(UUID.randomUUID().toString());
        req.setClassName(method.getDeclaringClass().getName());
        req.setMethodName(method.getName());
        req.setParameters(args);
        req.setTypeParameters(method.getParameterTypes());
        RpcFuture future = ch.send(req);
        return  future.get();
    }

}