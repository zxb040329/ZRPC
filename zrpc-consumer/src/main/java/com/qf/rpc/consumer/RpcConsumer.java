package com.qf.rpc.consumer;

import com.provider.rpc.codec.MyRpcProtocolDecoder;
import com.provider.rpc.codec.MyRpcProtocolEncoder;
import com.provider.rpc.core.RpcRequest;
import com.provider.rpc.core.RpcUtils;
import com.provider.rpc.core.ServiceMeta;
import com.provider.rpc.protocal.RpcProtocol;
import com.qf.rpc.handler.RpcResponseHandler;
import core.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import sun.util.resources.cldr.fur.CalendarData_fur_IT;

/**
 * @author zxb
 * @date 2021-04-23 17:42
 **/
@Slf4j
public class RpcConsumer {

    private final NioEventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    public RpcConsumer() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();
                        //Todo 差handler未添加
                        //添加自定义的编解码器
                        pipeline.addLast(new MyRpcProtocolEncoder());
                        pipeline.addLast(new MyRpcProtocolDecoder());

                        //添加自定义处理服务端响应信息的解码器
                        pipeline.addLast(new RpcResponseHandler());
                    }
                });
    }


    public void sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        //1.通过注册中心操作对象，获取到服务的对应元数据

        //1.1 获取到ServiceKey
        final String serviceMark = RpcUtils.buildServiceMark(protocol.getObject().getClassName(), protocol.getObject().getVersion());
        //1.2 获取到客户端的Hashcode（基于客户端的请求参数求hashcode，如果为null，则设为serviceKey的hashcode）
        final Object[] params = protocol.getObject().getParams();
        //Todo 改客户端hashcode值
//        int hashcode = params == null? serviceMark.hashCode() : params.hashCode();
        int hashcode = params.hashCode();
        //1.3  通过注册中心操作对象，获取到元数据
        final ServiceMeta serviceMeta = registryService.discoverService(serviceMark, hashcode);
        System.out.println("hashcode:" + hashcode + " serviceMeta:" + serviceMeta);


        //2.根据服务元数据的服务地址及端口、建立连接
        if (serviceMark != null) {
//            try {
                //Todo serviceMeta.getServiceAddress() 改为"127.0.0.1"
                final ChannelFuture future = bootstrap.connect(serviceMeta.getServiceAddress(), serviceMeta.getServicePort()).sync();

                //通过监听器来监控连接是否成功
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            log.info("connect remote service{} success on port{}", serviceMeta.getServiceAddress(), serviceMeta.getServicePort());
                        } else {
                            log.info("connect remote service{} failure on port{}", serviceMeta.getServiceAddress(), serviceMeta.getServicePort());
                            eventLoopGroup.shutdownGracefully();
                        }
                    }
                });

                //向远程服务发送请求

                future.channel().writeAndFlush(protocol);
//            } finally {
//                eventLoopGroup.shutdownGracefully();
//            }

        }

    }
}
