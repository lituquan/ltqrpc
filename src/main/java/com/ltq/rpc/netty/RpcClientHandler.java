package com.ltq.rpc.netty;

import com.ltq.rpc.code.Decoder;
import com.ltq.rpc.code.Encoder;
import com.ltq.rpc.code.Request;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {
	private int port;
	private String host;
	public Object response;
	public RpcClientHandler(String host, int port) {
		this.host=host;
		this.port=port;
	}
	public Object send(Request req){
	
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
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
		
			// Start the client.
			ChannelFuture f = b.connect(host,port).sync();   
			Channel ch=f.channel();
			ch.writeAndFlush(req).sync();//序列化
			ch.closeFuture().sync();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
		return null;
	}

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }
 
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		//接收报文    
    	this.response=msg;//反序列化
    }
 
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // channelsMap.put(new User(100,"张三"), ctx.channel());
    }
}