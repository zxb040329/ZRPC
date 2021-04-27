package com.provider.rpc.protocal;

/**
 * @author zxb
 * @date 2021-04-16 17:09
 **/
public class RpcProtocalConstants {

    /**
     * Todo 少加了final，成了变量
     */
    /**
     * magicNumber 2byte
     */
    public static final short MAGIC_NUMBER = 0x11;

    /**
     * protocolVersion
     */
    public static final byte PROTOCOL_VERSION = 0x22;


    /**
     * 数据长度 4byte
     */
    public static final int DATA_LENGTH = 18;

}
