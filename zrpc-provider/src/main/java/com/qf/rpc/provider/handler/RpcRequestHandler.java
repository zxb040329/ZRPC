package com.qf.rpc.provider.handler;

import com.provider.rpc.core.RpcRequest;
import com.provider.rpc.core.RpcResponse;
import com.provider.rpc.core.RpcUtils;
import com.provider.rpc.protocal.MessageHeader;
import com.provider.rpc.protocal.MessageTypeEnum;
import com.provider.rpc.protocal.RpcProtocol;
import com.provider.rpc.protocal.StatusEnum;
import com.qf.rpc.provider.cache.LocalCache;
import com.qf.rpc.provider.processor.BusinessThreadPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zxb
 * @date 2021-04-20 9:52
 **/
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> msg) throws Exception {

        final ThreadPoolExecutor threadPoolExecutor = BusinessThreadPool.getThreadPoolExecutor();
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
                //复用入参RpcProtocol的messageHeader
                final MessageHeader header = msg.getMessageHeader();
                //需要改的状态：messageType、status、dataLength(dataLength在MyRpcProtocolEncoder中有重新计算值，所以此处dataLength可以无值)
                //处理消息头
                header.setMessageType((byte) MessageTypeEnum.RESPONSE.getType());


                final RpcResponse rpcResponse = new RpcResponse();
                try {
                    header.setStatus((byte) StatusEnum.SUCCESSFUL.getType());
                    //处理消息体
                    Object result = handlerRequest(msg.getObject());
                    rpcResponse.setData(result);
                    responseRpcProtocol.setMessageHeader(header);
                    responseRpcProtocol.setObject(rpcResponse);
                } catch (Throwable throwable) {
                    header.setStatus((byte) StatusEnum.FAILURE.getType());
                    responseRpcProtocol.setMessageHeader(header);
                    rpcResponse.setData(null);
                    rpcResponse.setMsg(throwable.toString());
                    responseRpcProtocol.setObject(rpcResponse);
                    log.info("处理消息失败了{}",responseRpcProtocol);

                }finally {
                    ctx.channel().writeAndFlush(responseRpcProtocol);
                }


            }

        });

    }

    private Object handlerRequest(RpcRequest request) throws InvocationTargetException {
        final String key = RpcUtils.buildServiceMark(request.getClassName(), request.getVersion());
        final Object bean = LocalCache.map.get(key);
        if(bean == null){
            throw new RuntimeException("service is no exist ：" + request);
        }

        final FastClass fastClass = FastClass.create(bean.getClass());
        //通过索引的方式定位到具体方法
        final int index = fastClass.getIndex(request.getMethodName(), request.getParameterTypes());
        return fastClass.invoke(index,bean,request.getParams());
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.add(channel);
        log.info("有新的客户端连接上来：{},同时连接着{}个客户端",channel.remoteAddress(),channelGroup.size());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("客户端{}断开连接",channel.remoteAddress());

        channelGroup.remove(channel);
    }
}
