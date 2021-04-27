package com.provider.rpc.codec;

import com.provider.rpc.core.RpcRequest;
import com.provider.rpc.core.RpcResponse;
import com.provider.rpc.protocal.MessageHeader;
import com.provider.rpc.protocal.MessageTypeEnum;
import com.provider.rpc.protocal.RpcProtocalConstants;
import com.provider.rpc.protocal.RpcProtocol;
import com.provider.rpc.serialization.RpcSerialization;
import com.provider.rpc.serialization.exception.RpcSerializationException;
import com.provider.rpc.serialization.factory.RpcSerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zxb
 * @date 2021-04-17 18:01
 **/
//@Slf4j
public class MyRpcProtocolDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        /**
         * 魔数 2byte
         * 协议版本号 1byte
         * 序列化算法类型 1byte
         * 报文类型 1byte
         * 状态 1byte
         * 消息ID 8byte
         * 数据长度 4byte
         */

        int readableBytes = in.readableBytes();
        if(readableBytes >= RpcProtocalConstants.DATA_LENGTH){
            //记录初始的读指针位置
            in.markReaderIndex();
            //魔数
            short magicNumber = in.readShort();
            if(magicNumber != RpcProtocalConstants.MAGIC_NUMBER){
                in.resetReaderIndex();
                throw new RpcSerializationException("magicNumber错误" + magicNumber);
            }
            //协议版本号
            byte version = in.readByte();
            if(version != RpcProtocalConstants.PROTOCOL_VERSION){
                in.resetReaderIndex();
                throw new RpcSerializationException("PROTOCOL_VERSION错误" + version);
            }
            //序列化算法类型
            byte serializationType = in.readByte();
            //报文类型
            byte messageType = in.readByte();
            final MessageTypeEnum byType = MessageTypeEnum.findByType(messageType);
            if(byType == null){
                in.resetReaderIndex();
                throw new RpcSerializationException("messageType有问题" + byType);
            }
            //状态
            byte state = in.readByte();
            //消息ID
            long messId = in.readLong();

            int dateLength = in.readInt();
            if(readableBytes < dateLength){
                System.out.println("数据包未完整传输");
                //Todo log日志有问题
//                log.info("数据包未完整传输");
                //重置读指针位置
                in.resetReaderIndex();
                return;
            }

            //封装对象
            RpcProtocol rpcProtocol = new RpcProtocol();

            MessageHeader messageHeader = new MessageHeader();
            messageHeader.setMagicNumber(magicNumber);
            messageHeader.setProtocolVersion(version);
            messageHeader.setSerializationType(serializationType);
            messageHeader.setMessageType(messageType);
            messageHeader.setStatus(state);
            messageHeader.setMessId(messId);
            messageHeader.setDataLength(dateLength);

            //消息头
            rpcProtocol.setMessageHeader(messageHeader);

            byte[] bytes = new byte[dateLength];
            in.readBytes(bytes);
            RpcSerialization init = RpcSerializationFactory.init(serializationType);
            //消息体
            switch (byType){
                case REQEUST:
                    final RpcRequest rpcRequest = init.deSerializer(bytes, RpcRequest.class);
                    rpcProtocol.setObject(rpcRequest);
                    out.add(rpcProtocol);
                    //Todo 少个break
                    break;
                case RESPONSE:
                    final RpcResponse rpcResponse = init.deSerializer(bytes, RpcResponse.class);
                    rpcProtocol.setObject(rpcResponse);
                    //Todo rpcResponse改成rpcProtocol
                    out.add(rpcProtocol);
                    break;
            }


        }
    }
}
