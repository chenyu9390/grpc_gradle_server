package net.gichain.rechargeChannel;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@SuppressWarnings("unchecked")
@Component
public class SpringUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    public void setApplicationContext(ApplicationContext applicationContext){
        if(SpringUtils.applicationContext == null){
            SpringUtils.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    public static<T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    public static<T> T getBean(String name, Class<T> clazz){
        return (T)getApplicationContext().getBean(name);
    }
}
