package com.ltq.rpc.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.UUID;

import com.ltq.rpc.code.Request;

public class ClientProxy<T> implements InvocationHandler {
    private Socket socket;
        
    ObjectInputStream in = null;
    ObjectOutputStream out = null;

    public ClientProxy(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        out=new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(req);
        out.flush();

        in=new ObjectInputStream(socket.getInputStream());
        return in.readObject();
    }

}