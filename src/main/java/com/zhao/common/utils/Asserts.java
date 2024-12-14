package com.zhao.common.utils;

import com.zhao.common.exception.BusinessException;
import com.zhao.common.respvo.ResponseStatus;
import org.apache.commons.lang3.StringUtils;

public class Asserts {

    private Asserts(){}

    public static void notNull(Object o){
        if (o == null)
            throw new BusinessException(ResponseStatus.INVALIDE_PARAMS);
    }

    public static void notNull(Object o, ResponseStatus status){
        if (o == null)
            throw new BusinessException(status);
    }

    public static void notNull(Object o, String message){
        if (o == null)
            throw new BusinessException(message);
    }

    public static void notNull(Object o, int code, String message){
        if (o == null)
            throw new BusinessException(code, message);
    }

    public static void notEmpty(String o){
        if (StringUtils.isEmpty(o))
            throw new BusinessException(ResponseStatus.INVALIDE_PARAMS);
    }

    public static void notEmpty(String o, ResponseStatus status){
        if (StringUtils.isEmpty(o))
            throw new BusinessException(status);
    }

    public static void notEmpty(String o, String message){
        if (StringUtils.isEmpty(o))
            throw new BusinessException(message);
    }

    public static void state(boolean state, String message){
        if (!state)
            throw new BusinessException(message);
    }

}
