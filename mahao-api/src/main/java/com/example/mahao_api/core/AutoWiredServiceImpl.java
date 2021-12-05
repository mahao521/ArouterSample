package com.example.mahao_api.core;

import android.content.Context;
import android.service.autofill.AutofillService;

import com.example.mahao_annotation.annotation.Route;
import com.example.mahao_api.Consts;
import com.example.mahao_api.service.AutoWireService;
import com.example.mahao_api.templete.ISyringe;

import java.lang.reflect.InvocationTargetException;

@Route(path = "/lisi/service/autowired")
public class AutoWiredServiceImpl implements AutoWireService {


    @Override
    public void autoWire(Object instance) {
        doinject(instance,null);
    }

    @Override
    public void init(Context context) {

    }


    private void  doinject(Object object,Class parent){
        Class clazz = parent == null ? object.getClass() : parent;
        try {
            ISyringe iSyringe = (ISyringe) Class.forName(clazz.getName()+ Consts.SUFFIX_AUTOWIRED).getConstructor().newInstance();
            if(iSyringe != null){
                iSyringe.inject(object);
            }
            Class superclass = clazz.getSuperclass();
            if(null != superclass && !superclass.getName().startsWith("android")) {
                doinject(object, superclass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
