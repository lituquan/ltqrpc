package com.ltq.rpc.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // TODO Auto-generated method stub
        //使用java默认序列化
        byte[] byteArray = ByteUtils.toByteArray(msg);
        out.writeInt(byteArray.length);
        out.writeBytes(byteArray);
    }

}