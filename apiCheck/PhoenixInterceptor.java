package com.jd.market.interact.web.interceptor;

import com.jd.common.struts.interceptor.JdInterceptor;
import com.jd.market.interact.service.sign.CheckService;
import com.opensymphony.xwork2.ActionInvocation;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by maoyi on 2016/9/5.
 */
public class PhoenixInterceptor extends JdInterceptor {
    @Resource
    private CheckService checkService;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if(null == invocation.getProxy() || !invocation.getProxy().getNamespace().equals("/check") ||  !invocation.getProxy().getActionName().equals("check_doCheck")){
            return invocation.invoke();
        }
        Map<String, Object> parameters = invocation.getInvocationContext().getParameters();
        if(!parameters.containsKey("className") || !parameters.containsKey("methodName")){
            return invocation.invoke();
        }
        String className = ((String[]) (parameters.get("className")))[0];
        String methodName = ((String[]) (parameters.get("methodName")))[0];
        String msg = checkService._invoke(className, methodName, parameters);
        invocation.getInvocationContext().getValueStack().set("msg",msg);
        return invocation.invoke();
    }
}
