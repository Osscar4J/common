package com.zhao.common.utils;

import groovy.lang.GroovyClassLoader;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName: MyClassFactory
 * @Author: zhaolianqi
 * @Date: 2022/6/27 17:23
 * @Version: v1.0
 */
public class MyClassFactory {

    private static MyClassFactory classFactory = new MyClassFactory();
    public static MyClassFactory getInstance(){
        return classFactory;
    }

    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
    private ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

    public <T> T loadNewInstance(String sourceCode, Class<T> interfaceClass) throws Exception{
        if (sourceCode!=null && sourceCode.trim().length()>0) {
            Class<?> clazz = getCodeSourceClass(sourceCode);
            if (clazz != null) {
                Object instance = clazz.newInstance();
                if (instance!=null) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    boolean isSub = false;
                    for (Class<?> anInterface : interfaces) {
                        if (anInterface == interfaceClass){
                            isSub = true;
                            break;
                        }
                    }
                    if (!isSub){
                        if (isSubClass(clazz, interfaceClass))
                            isSub = true;
                    }
                    if (isSub) {
                        return (T) instance;
                    } else {
                        throw new IllegalArgumentException(">>>>>>>>>>> loadNewInstance error, cannot convert from instance["+ instance.getClass() +"] to IJobHandler");
                    }
                }
            }
        }
        throw new IllegalArgumentException(">>>>>>>>>>> loadNewInstance error, instance is null");
    }

    /**
     * 校验sub是不是继承自parent
     * @Author zhaolianqi
     * @Date 2022/6/27 17:57
     */
    public boolean isSubClass(Class<?> sub, Class<?> parent){
        sub = sub.getSuperclass();
        if (sub == Object.class){
            return false;
        }
        if (sub == parent)
            return true;
        return isSubClass(sub, parent);
    }

    private Class<?> getCodeSourceClass(String sourceCode){
        try {
            // md5
            byte[] md5 = MessageDigest.getInstance("MD5").digest(sourceCode.getBytes());
            String md5Str = new BigInteger(1, md5).toString(16);

            Class<?> clazz = CLASS_CACHE.get(md5Str);
            if(clazz == null){
                clazz = groovyClassLoader.parseClass(sourceCode);
                CLASS_CACHE.putIfAbsent(md5Str, clazz);
            }
            return clazz;
        } catch (Exception e) {
            return groovyClassLoader.parseClass(sourceCode);
        }
    }

}
