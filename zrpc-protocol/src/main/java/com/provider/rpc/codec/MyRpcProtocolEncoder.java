package com.provider.rpc.codec;

import com.provider.rpc.protocal.MessageHeader;
import com.provider.rpc.protocal.RpcProtocol;
import com.provider.rpc.serialization.RpcSerialization;
import com.provider.rpc.serialization.factory.RpcSerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zxb
 * @date 2021-04-17 17:40
 **/
public class MyRpcProtocolEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol msg, ByteBuf out) {
        MessageHeader messageHeader = msg.getMessageHeader();
        Object object = msg.getObject();
        /**
         * 魔数 2byte
         * 协议版本号 1byte
         * 序列化算法类型 1byte
         * 报文类型 1byte
         * 状态 1byte
         * 消息ID 8byte
         * 数据长度 4byte
         */

        //消息头
        out.writeShort(messageHeader.getMagicNumber());
        out.writeByte(messageHeader.getProtocolVersion());
        out.writeByte(messageHeader.getSerializationType());
        out.writeByte(messageHeader.getMessageType());
        out.writeByte(messageHeader.getStatus());
        out.writeLong(messageHeader.getMessId());

        RpcSerialization init = RpcSerializationFactory.init(messageHeader.getSerializationType());
        byte[] serializer = init.serializer(object);

        out.writeInt(serializer.length);

        //消息体
        out.writeBytes(serializer);

    }
}
