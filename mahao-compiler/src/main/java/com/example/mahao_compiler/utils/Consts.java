package com.example.mahao_compiler.utils;

import com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat;

public class Consts {

    public static final String PROJECT = "ARouter";

    public static final String ACTIVITY = "android.app.Activity";
    public static final String SERVICE = "android.app.Service";
    public static final String FRAGMENT = "android.app.Fragment";
    public static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    public static final String PARCELABLE = "android.os.Parcelable";
    public static final String SERIALIZABLE = "java.io.Serializable";
    public static final String PROVIDER = "com.example.mahao_api.templete.IProvider";
    public static final String GROUP = "com.example.mahao_api.templete.IRouteGroup";
    public static final String ROOT = "com.example.mahao_api.templete.IRouteRoot";
    public static final String PROVIDER_GROUP = "com.example.mahao_api.templete.IProviderGroup";

    public static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String CHAR = LANG + ".Character";
    public static final String STRING = LANG + ".String";
    public static final String METHOD_LOAD_INTO = "loadInto";
    public static final String SEPARATOR = "_";
    public static final String METHOD_INJECT = "inject";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root";
    public static final String NAME_OF_PROVIDER = PROJECT + SEPARATOR + "Providers";
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;
    public static final String NAME_OF_INTERCEPTOR = PROJECT + SEPARATOR + "Interceptors";
    public static final String NAME_OF_AUTOWIRED = SEPARATOR + PROJECT + SEPARATOR + "Autowired";
    public static final String ISYRING = "com.example.mahao_api.templete.ISyringe";
    public static final String JSON_SERVICE = "com.example.mahao_api.service.SerializationService";
    public static final String TYPE_WRAPPER = "com.example.mahao_annotation.model.TypeWrapper";


    static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";
    public static final String KEY_MODULE_NAME = "AROUTER_MODULE_NAME";
    public static final String ANNOTATION_PACKAGE = "com.example.mahao_annotation";
    public static final String ANNOTATION_ROUTE = ANNOTATION_PACKAGE + ".annotation.Route";
    public static final String ANNOTATION_AUTOWIRED = ANNOTATION_PACKAGE + ".annotation.Autowired";
    public static final String PACKAGE_OF_GENERATE_FILE = "com.example.mahao.routes";

}
