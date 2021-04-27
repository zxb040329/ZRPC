package com.qf.rpc.provider.provider;

import com.provider.rpc.codec.MyRpcProtocolDecoder;
import com.provider.rpc.codec.MyRpcProtocolEncoder;
import com.provider.rpc.core.RpcUtils;
import com.provider.rpc.core.ServiceMeta;
import com.qf.rpc.provider.annotation.RpcService;
import com.qf.rpc.provider.cache.LocalCache;
import com.qf.rpc.provider.handler.RpcRequestHandler;
import com.qf.rpc.provider.properties.RpcProperties;
import core.RegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1.启动本地服务
 * 2.将服务发布的注册中心
 * @author zxb
 * @date 2021-04-19 22:42
 **/
@Data
@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private RegistryService registryService;
    private int servicePort;

    @Resource
    private RpcProperties rpcProperties;


    public RpcProvider(RegistryService registryService, int servicePort) {
        this.registryService = registryService;
        this.servicePort = servicePort;
    }

    private int beanNum;
    private AtomicInteger beanAtomicIn = new AtomicInteger(0);

    /**
     * 当Properties属性注入后会执行此方法（当Properties属性注入之后的设置）
     */
    @Override
    public void afterPropertiesSet() {

        new Thread(() -> {
            final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            final NioEventLoopGroup workGroup = new NioEventLoopGroup();

            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) {
                            final ChannelPipeline pipeline = ch.pipeline();
                            //添加自定义的编解码器
                            pipeline.addLast(new MyRpcProtocolEncoder());
                            pipeline.addLast(new MyRpcProtocolDecoder());

                            //添加处理客户端请求的处理器
                            pipeline.addLast(new RpcRequestHandler());

                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            try {
                final ChannelFuture future = serverBootstrap.bind(servicePort).sync();
                log.info("服务器已启动，正监听{}端口中",servicePort);

                //程序监听NioServerSocketChannel的关闭事件并同步阻塞当前线程
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }

        }).start();
    }


    /**
     * 当初始化bean完毕之后
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        beanNum++;
        beanAtomicIn.incrementAndGet();
        //1.扫描bean是否包含了@RpcService注解，获取其注解bean
        final RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if(rpcService != null){
            log.info("IOC容器总共构建了{}个bean",beanNum);
            log.info("IOC容器总共构建了{}个bean(AtomicInteger)",beanAtomicIn.get());
            //2.如果包含，则构建出服务元信息并将其注册到注册中心
            final ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceName(rpcService.serviceInterface().getName());
            serviceMeta.setServicePort(servicePort);
            //Todo ServiceAddress从rpcProperties.getRegistryAddr()改成127.0.0.1
            serviceMeta.setServiceAddress("127.0.0.1");
            serviceMeta.setVersion(rpcService.version());

            try {
                registryService.register(serviceMeta);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //3.本地缓存一份bean，key为ServiceMark，value为bean
            //Todo rpcService.getClass().getName() 改为 rpcService.serviceInterface().getName()
            LocalCache.map.put(RpcUtils.buildServiceMark(rpcService.serviceInterface().getName(),rpcService.version()),bean);

        }
        return bean;
    }
}
