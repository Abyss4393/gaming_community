package cn.abyss4393.utils.inject;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className BeanInjectionUtils
 * @description TODO
 * @date 3/9/2023
 */

@Component
public final class BeanInjectionUtils implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static ConfigurableListableBeanFactory  beanFactory;
    private static ApplicationContext context;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanInjectionUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        BeanInjectionUtils.context = context;
    }

    public static <T> T getBean(Class<T> cl) throws BeansException{
        return (T) beanFactory.getBean(cl);
    }

    public static boolean containsBean(String name)
    {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @return boolean
     *
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.isSingleton(name);
    }

    /**
     * @return Class 注册对象的类型
     *
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     *
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取aop代理对象
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker)
    {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     */
    public static String[] getActiveProfiles()
    {
        return BeanInjectionUtils.context.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取当前的环境配置，当有多个环境配置时，只获取第一个
     *
     * @return 当前的环境配置
     */
    public static String getActiveProfile()
    {
        final String[] activeProfiles = getActiveProfiles();
        return !StringUtils.checkValNull(activeProfiles) ? activeProfiles[0] : null;
    }
}
