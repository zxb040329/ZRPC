package core;

import lombok.Getter;

/**
 * @author zxb
 * @date 2021-04-18 16:05
 **/
public enum RegistryTypeEnum {
    ZOOKEEPER(1),EUREKA(2),NACOS(3);

    @Getter
    int type;

    RegistryTypeEnum(int type){
        this.type = type;
    }

    public static RegistryTypeEnum findByType(int type){
        final RegistryTypeEnum[] values = RegistryTypeEnum.values();
        for (RegistryTypeEnum value : values) {
            if(value.type == type){
                return value;
            }
        }
        return null;
    }


}
