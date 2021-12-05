package com.example.mahao_api.service;

import com.example.mahao_api.templete.IProvider;

import java.lang.reflect.Type;

public interface SerializationService extends IProvider {

    /**
     * parse json to object
     *
     * @param input
     * @param classz
     * @param <T>
     * @return
     */
    <T> T json2Object(String input, Class<T> classz);


    /**
     * object to json
     *
     * @param instance
     * @return
     */
    String object2Json(Object instance);


    /**
     * parse joson to object
     *
     * @param input  json string
     * @param classz object type
     * @param <T>
     * @return
     */
    <T> T parseObject(String input, Type classz);

}
