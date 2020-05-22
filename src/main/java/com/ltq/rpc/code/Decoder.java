package com.ltq.rpc.code;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // TODO Auto-generated method stub
		int length=in.readInt();
        byte[] data = new byte[length];
        in.readBytes(data);
        //使用java默认序列化
        out.add(ByteUtils.BytetoArray(data));
    }

}