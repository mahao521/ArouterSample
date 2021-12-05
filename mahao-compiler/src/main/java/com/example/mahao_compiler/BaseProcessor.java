package com.example.mahao_compiler;

import com.example.mahao_compiler.utils.Consts;
import com.example.mahao_compiler.utils.Logger;
import com.example.mahao_compiler.utils.TypeUtils;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class BaseProcessor extends AbstractProcessor {
    Filer filer;
    Logger logger;
    Types types;
    Elements elementUtils;
    TypeUtils typeUtils;
    String moudleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        logger = new Logger(processingEnv.getMessager());
        types = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = new TypeUtils(types, elementUtils);
        Map<String,String> options = processingEnv.getOptions();
        if(MapUtils.isNotEmpty(options)){
            moudleName = options.get(Consts.KEY_MODULE_NAME);
        }
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new TreeSet<String>() {
            {
                add(Consts.KEY_MODULE_NAME);
            }
        };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
