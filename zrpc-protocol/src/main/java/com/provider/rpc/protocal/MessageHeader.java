package com.provider.rpc.protocal;

import lombok.Data;

/**
 * 协议头封装类
 * @author zxb
 * @date 2021-04-16 15:35
 **/
@Data
public class MessageHeader {
    /**
     * 魔数 2byte
     * 协议版本号 1byte
     * 序列化算法类型 1byte
     * 报文类型 1byte
     * 状态 1byte
     * 消息ID 8byte
     * 数据长度 4byte
     */

    /**
     * 魔数
     */
    private short magicNumber;

    /**
     * 协议版本号
     */
    private byte protocolVersion;

    /**
     * 序列化类型
     */
    private byte serializationType;

    /**
     * 报文类型
     */
    private byte messageType;

    /**
     * 状态
     */
    private byte status;

    /**
     * 消息ID
     */
    private long messId;

    /**
     * 数据长度
     */
    private int dataLength;

}
