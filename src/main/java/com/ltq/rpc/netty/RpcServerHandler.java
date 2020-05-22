package com.ltq.rpc.netty;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.ltq.rpc.code.Request;
import com.ltq.rpc.register.ServiceRegistry;
import com.ltq.rpc.register.SimpleMapRegistry;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcServerHandler extends ChannelInboundHandlerAdapter {
	private static final HashMap<String, Class<?>> serviceImpl = new HashMap();
	private static  ServiceRegistry serviceRegistry = new SimpleMapRegistry();

	public static void register(Class serviceInterface, Class impl,String  addr) {
		serviceImpl.put(serviceInterface.getName(), impl);
		serviceRegistry.register(serviceInterface.getName(),addr);
	}
	
	public static void  setServiceRegistry(ServiceRegistry serviceRegistry0){
		serviceRegistry=serviceRegistry0;
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		// 读报文
		Request req = (Request) msg;
		Object result=handle(req);
		// 写报文
		ctx.writeAndFlush(result).addListener(ChannelFutureListener.CLOSE);

	}
  
	private Object handle(Request req) {
		// 执行方法
		String serviceName = req.getClassName();
		String methodName = req.getMethodName();
		Class<?>[] parameterTypes = (Class<?>[]) req.getTypeParameters();
		Object[] arguments = (Object[]) req.getParameters();
		// 代理方法
		Class serviceClass = serviceImpl.get(serviceName);
		try {
			if (serviceClass == null) {
				throw new ClassNotFoundException(serviceName + " not found");
			}
			Method method = serviceClass.getMethod(methodName, parameterTypes);
			Object result = method.invoke(serviceClass.newInstance(), arguments);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}