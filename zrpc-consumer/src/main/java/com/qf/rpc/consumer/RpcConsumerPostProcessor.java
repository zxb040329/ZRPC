package com.qf.rpc.consumer;

import com.qf.rpc.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zxb
 * @date 2021-04-23 18:24
 **/
@Component
public class RpcConsumerPostProcessor implements BeanFactoryPostProcessor, BeanClassLoaderAware, ApplicationContextAware {

    private ClassLoader classLoader;

    private ApplicationContext context;

    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }


    /**
     * 在bean初始化之前做的事
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        //1.从beanFactory获得所有的bean的定义信息
        final String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

        for (String beanDefinitionName : beanDefinitionNames) {
            //2.逐个去获取bean的定义信息
            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            //3.获取bean的类名称
            final String beanClassName = beanDefinition.getBeanClassName();

            if(beanClassName != null) {
                //4.通过ClassUtils工具类及bean的名称去获取对应的Class对象
                final Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
                //5.通过ReflectionUtils获取到Class对象所有的Field属性
                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        parseRpcReference(field);
                    }
                });
            }
        }

        //6.解析完毕后，将bean注册到IOC容器中 registry.registryBeanDefinition(beanName,beanDef)
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        beanDefinitionMap.forEach((key,value)->{
            if(context.containsBean(key)){
                throw new IllegalArgumentException("Spring context already has bean" + key);
            }
            defaultListableBeanFactory.registerBeanDefinition(key,value);
        });



    }

    private void parseRpcReference(Field field) {
        //1.通过AnnotationUtils获取到当前属性的注解信息；
        final RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);

        //2.当注解信息对象不为空
        if(annotation != null){
            //通过BeanDefinitionBuilder.genericBeanDefinition()获得builder对象
            //3.设置初始化方法、为其成员变量赋值 （其中interfaceClass ：filed.getType()）
            final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            builder.setInitMethodName("init");
            builder.addPropertyValue("interfaceClass",field.getType());
            builder.addPropertyValue("registryType",annotation.registryType());
            builder.addPropertyValue("registryAddr",annotation.registryAddr());
            builder.addPropertyValue("version",annotation.version());
            builder.addPropertyValue("timeout",annotation.timeout());

            final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

            //4.通过builder对象获得BeanDefinition并存储到map当中；

            beanDefinitionMap.put(field.getName(),beanDefinition);
        }

    }

}
