package com.ltq.rpc.netty;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.ltq.rpc.code.Request;
import com.ltq.rpc.code.Response;
import com.ltq.rpc.register.ServiceRegistry;
import com.ltq.rpc.register.SimpleMapRegistry;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcServerHandler extends ChannelInboundHandlerAdapter {
	private static final HashMap<String, Object> serviceImpl = new HashMap();
	private static  ServiceRegistry serviceRegistry = new SimpleMapRegistry();

	public static void register(Class serviceInterface, Class serviceClass,String  addr) {
		try {
			serviceImpl.put(serviceInterface.getName(), serviceClass.newInstance());
			serviceRegistry.register(serviceInterface.getName(),addr);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void  setServiceRegistry(ServiceRegistry serviceRegistry0){
		serviceRegistry=serviceRegistry0;
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
		// 读报文
		Request req = (Request) msg;
		Object result=handle(req);
		// 写报文
		ctx.writeAndFlush(result).sync();
	}
  
	private Object handle(Request req) {
		// 执行方法
		String serviceName = req.getClassName();
		String methodName = req.getMethodName();
		Class<?>[] parameterTypes = (Class<?>[]) req.getTypeParameters();
		Object[] arguments = (Object[]) req.getParameters();
		// 代理方法
		Object serviceClass = serviceImpl.get(serviceName);
		try {
			if (serviceClass == null) {
				throw new ClassNotFoundException(serviceName + " not found");
			}
			Method method = serviceClass.getClass().getMethod(methodName, parameterTypes);
			Object result = method.invoke(serviceClass, arguments);
			Response response=new Response();
			response.setResult(result);
			response.setMessageId(req.getMessageId());
			return response;
		} catch (Exception e) {
			Response response=new Response();
			response.setError("server error");
			response.setMessageId(req.getMessageId());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("Error:"+cause.getMessage());
		ctx.close();
	}

}