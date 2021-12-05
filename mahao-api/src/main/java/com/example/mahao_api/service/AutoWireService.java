package com.example.mahao_api.service;

import com.example.mahao_api.templete.IProvider;

public interface AutoWireService extends IProvider {

    /**
     * the instance who need autowired
     *
     * @param instance
     */
    void autoWire(Object instance);
}
