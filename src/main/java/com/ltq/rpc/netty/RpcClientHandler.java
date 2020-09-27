package com.ltq.rpc.netty;

import com.ltq.rpc.code.Decoder;
import com.ltq.rpc.code.Encoder;
import com.ltq.rpc.code.Request;

import com.ltq.rpc.code.Response;
import com.ltq.rpc.register.ServiceRegistry;
import com.ltq.rpc.register.SimpleMapRegistry;
import com.ltq.rpc.socket.RpcFuture;
import com.ltq.rpc.test.Hello;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);
	private volatile Channel channel;
	private ServiceRegistry registry=new SimpleMapRegistry();
	private ConcurrentHashMap<String, RpcFuture> pendingRPC = new ConcurrentHashMap<>();
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

	public RpcClientHandler(ServiceRegistry registry) {
		this.registry=registry;
		//服务发现：假设当前只有一个服务地址  ip：port
		//这里可以做客户端负载均衡
		String remotePeer = registry.discover(Hello.class.getName());
		Bootstrap b = new Bootstrap();
		b.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						p.addLast(new Encoder());
						p.addLast(new Decoder());
						p.addLast(RpcClientHandler.this);
					}
				});
		String[] split = remotePeer.split(":");
		try {
			this.channel= b.connect(split[0],Integer.valueOf(split[1])).sync().channel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public RpcFuture send(Request request) throws InterruptedException {
		RpcFuture rpcFuture = new RpcFuture(request);
		pendingRPC.put(request.getMessageId(), rpcFuture);
		channel.writeAndFlush(request).sync();
		return rpcFuture;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		this.channel = ctx.channel();
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		Response response= (Response) msg;
		String requestId = response.getMessageId();
		logger.debug("Receive response: " + requestId);
		RpcFuture rpcFuture = pendingRPC.get(requestId);
		if (rpcFuture != null) {
			pendingRPC.remove(requestId);
			rpcFuture.done(response);
		} else {
			logger.warn("Can not get pending response for request id: " + requestId);
		}
    }
 
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

    }
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }
}