package com.jd.market.interact.service.sign;

import java.util.Map;

/**
 * Created by maoyi on 2016/9/6.
 */
public interface CheckService {
    String _invoke(String className, String methodName, Map<String, Object> parameters);
}
