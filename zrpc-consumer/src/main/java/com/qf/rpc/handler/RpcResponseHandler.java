package com.qf.rpc.handler;

import com.provider.rpc.core.RpcFuture;
import com.provider.rpc.core.RpcRequest;
import com.provider.rpc.core.RpcResponse;
import com.provider.rpc.protocal.MessageHeader;
import com.provider.rpc.protocal.RpcProtocol;
import com.qf.rpc.holder.RpcRequestHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author zxb
 * @date 2021-04-23 20:27
 **/
//Todo RpcRequest改为RpcResponse
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) {

        //接收到服务端的处理结果后，将其结果设置到消息对应的future中
        final MessageHeader messageHeader = protocol.getMessageHeader();
        final long messId = messageHeader.getMessId();
        final RpcFuture rpcFuture = RpcRequestHolder.REQUEST_MAP.get(messId);
        //Todo protocol.getObject()改protocol.getObject().getData()
        rpcFuture.getPromise().setSuccess(protocol.getObject());

    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("111");
//        RpcProtocol<RpcResponse> protocol = (RpcProtocol<RpcResponse>) msg;
//        final MessageHeader messageHeader = protocol.getMessageHeader();
//        final long messId = messageHeader.getMessId();
//        final RpcFuture rpcFuture = RpcRequestHolder.REQUEST_MAP.get(messId);
//        rpcFuture.getPromise().setSuccess(protocol.getObject());
//    }
}
