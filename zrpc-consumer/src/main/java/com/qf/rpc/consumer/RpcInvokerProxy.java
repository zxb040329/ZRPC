package com.qf.rpc.consumer;

import com.provider.rpc.core.RpcFuture;
import com.provider.rpc.core.RpcRequest;
import com.provider.rpc.core.RpcResponse;
import com.provider.rpc.protocal.*;
import com.qf.rpc.holder.RpcRequestHolder;
import core.RegistryService;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author zxb
 * @date 2021-04-23 17:18
 **/
public class RpcInvokerProxy implements InvocationHandler {

    private RegistryService registryService;
    private String version;
    private long timeout;

    public RpcInvokerProxy(RegistryService registryService, String version, long timeout) {
        this.registryService = registryService;
        this.version = version;
        this.timeout = timeout;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //1.构建自定义协议包
        final RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();

        //2.构建消息头
        final MessageHeader header = new MessageHeader();
        /**
         * 魔数 2byte magicNumber
         * 协议版本号 1byte protocolVersion
         * 序列化算法类型 1byte serializationType
         * 报文类型 1byte messageType
         * 状态 1byte status  (成功)
         * 消息ID 8byte messId
         * 数据长度 4byte dataLength
         */
        header.setMagicNumber(RpcProtocalConstants.MAGIC_NUMBER);
        //Todo 版本号弄错成魔数
        header.setProtocolVersion((byte) RpcProtocalConstants.PROTOCOL_VERSION);
        header.setSerializationType((byte) RpcSerializationTypeEnum.HESSION.getType());
        header.setMessageType((byte) MessageTypeEnum.REQEUST.getType());
        header.setStatus((byte) StatusEnum.SUCCESSFUL.getType());
        header.setMessId(RpcRequestHolder.ATOMIC_LONG.getAndIncrement());

        //3.构建消息体
        final RpcRequest rpcRequest = new RpcRequest();
        //Todo 不清楚setClassName、setMethodName、setParameterTypes
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParams(args);
        rpcRequest.setVersion(version);

        //4.设置消息头和消息体
        protocol.setMessageHeader(header);
        protocol.setObject(rpcRequest);

        //5.借助RpcConsumer来完成服务的真正调用
        final RpcConsumer consumer = new RpcConsumer();
        Object result = null;
        int retryNum = 3;

        //增加失败重试机制
        do {
            consumer.sendRequest(protocol, registryService);
            //Future 异步保存结果 Todo future+map的应用，值得再细看
            final RpcFuture<RpcResponse> future = new RpcFuture<>(
                    new DefaultPromise<>(new DefaultEventLoop()), timeout
            );
            RpcRequestHolder.REQUEST_MAP.put(header.getMessId(), future);

            //等待结果
            try {
                result = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
            } catch (Exception e) {
                //捕获超时异常，但不处理
                System.out.println("超时异常");
                retryNum--;
            }
        } while (result == null && retryNum >= 0);

        //Todo
        return result;
    }
}
