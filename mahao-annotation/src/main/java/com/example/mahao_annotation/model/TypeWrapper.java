package com.example.mahao_annotation.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeWrapper<T> {

    protected Type type;

    protected TypeWrapper() {
        Type superClass = getClass().getGenericSuperclass();
        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
