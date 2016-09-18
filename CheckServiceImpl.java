package com.jd.market.interact.service.sign.impl;

import com.jd.market.interact.domain.util.AppContextHolder;
import com.jd.market.interact.domain.util.JsonUtils;
import com.jd.market.interact.service.sign.CheckService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maoyi on 2016/9/6.
 */
@Component("checkService")
public class CheckServiceImpl implements CheckService{
    @Override
    public String _invoke(String className, String methodName, Map<String, Object> parameters){
        try {
            Class<?> aClass = Class.forName(className);
            Method method = getMethod(aClass,methodName);
            if(null == method){
                return null;
            }
            Object bean = getBean(aClass);
            Object invoke = method.invoke(bean, invoke_args(method, parameters));
            return JsonUtils.objToJsonString(invoke);
        } catch (Exception e) {
            if(e instanceof InvocationTargetException){
                return ((InvocationTargetException) e).getTargetException().toString();
            }else{
                return e.toString();
            }
        }
    }

    private Object getBean(Class clazz){
        String simpleName = clazz.getSimpleName();
        String beanName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        return AppContextHolder.getBean(beanName);
    }

    private Method getMethod(Class clazz , String methodName){
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if(method.getName().equals(methodName)){
                return method;
            }
        }
        return null;
    }

    private Object[] invoke_args(Method method , Map<String,Object> map) throws NoSuchFieldException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] objs = new Object[parameterTypes.length];
        int i =0 ;
        for (Class<?> type : parameterTypes) {
            objs[i++] = getInstance(type,map);
        }
        return objs;
    }

    private Object getInstance(Class<?> type, Map<String, Object> map) throws InvocationTargetException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        Map<String,String> param = new HashMap<>();
        Object o = type.newInstance();
        for (String s : map.keySet()) {
            String simpleName = type.getSimpleName();
            String[] key = s.split("\\.");
            if(key.length == 1 || !key[0].equals(simpleName)){continue;}
            Field field = type.getDeclaredField(key[1]);
            int modifiers = field.getModifiers();
            String string = Modifier.toString(modifiers);
            if(string.contains("final")){
                continue;
            }
            param.put(key[1],((String[])(map.get(s)))[0]);
        }
        BeanUtils.populate(o,param);
        return o;
    }
}
