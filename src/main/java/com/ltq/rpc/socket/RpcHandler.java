package com.ltq.rpc.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

import com.ltq.rpc.code.Request;
import com.ltq.rpc.register.ServiceContainer;

public class RpcHandler implements Runnable {
    private Socket socket=null;
  
    public RpcHandler(Socket socket){
        this.socket=socket;
    }

    public void run() {

        try {
            //通过while循环不断读取信息，
            while (true) {
                //读报文
                //转对象 -- 使用java默认反序列化
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Request req= (Request) input.readObject();
                //反射获取结果
                Object result=handle(req);            
                //转报文 -- 使用java默认序列化
                //写报文             
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(result);             
            }
        } catch (Exception e) {
        	 e.printStackTrace();
        }

    }

    private Object handle(Request req) throws Exception {
        //接口,方法+参数类型,参数
        String serviceName = req.getClassName();
        String methodName = req.getMethodName();
        Class<?>[] parameterTypes = (Class<?>[]) req.getTypeParameters();
        Object[] arguments = (Object[]) req.getParameters();              
        //代理方法
        Class serviceClass = ServiceContainer.get(serviceName);
        if (serviceClass == null) {
            throw new ClassNotFoundException(serviceName + " not found");
        }
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        Object result = method.invoke(serviceClass.newInstance(), arguments);   
        return result;
    }
}

class ServiceInvocation  implements InvocationHandler {

    private Object target;

    public ServiceInvocation(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("判断用户是否有权限进行操作");
        Object obj = method.invoke(target);
        System.out.println("记录用户执行操作的用户信息、更改内容和时间等");
        return obj;
    }
}