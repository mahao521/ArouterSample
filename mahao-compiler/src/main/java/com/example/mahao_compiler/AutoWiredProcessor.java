package com.example.mahao_compiler;

import com.example.mahao_annotation.annotation.Autowired;
import com.example.mahao_compiler.utils.Consts;
import com.example.mahao_compiler.utils.TypeKind;
import com.example.mahao_compiler.utils.TypeUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.org.apache.xpath.internal.operations.Bool;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
@SupportedAnnotationTypes({Consts.ANNOTATION_AUTOWIRED})
public class AutoWiredProcessor extends BaseProcessor {

    //key父类   value ： 注解标注的field
    private Map<TypeElement, List<Element>> parentAndChild = new HashMap<>();
    private static final ClassName ARouterClass = ClassName.get("com.example.mahao_api", "Arouter");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger.info("=====  AutoWiredProcessor init . <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!CollectionUtils.isEmpty(annotations)) {
            try {
                categories(roundEnv.getElementsAnnotatedWith(Autowired.class));
                generHelper();
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(">>   >>" + e.toString());
            }
            return true;
        }
        return false;
    }

    private void generHelper() {
        TypeElement type_ISyringe = elementUtils.getTypeElement(Consts.ISYRING);
        TypeElement type_JsonService = elementUtils.getTypeElement(Consts.JSON_SERVICE);
        TypeMirror typeProvider = elementUtils.getTypeElement(Consts.PROVIDER).asType();
        TypeMirror typeActivity = elementUtils.getTypeElement(Consts.ACTIVITY).asType();
        TypeMirror typeFragment = elementUtils.getTypeElement(Consts.FRAGMENT).asType();
      //  TypeMirror typeFragmentV4 = elementUtils.getTypeElement(Consts.FRAGMENT_V4).asType();

        //params
        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, "target").build();
        if (MapUtils.isNotEmpty(parentAndChild)) {

            for (Map.Entry<TypeElement, List<Element>> entry : parentAndChild.entrySet()) {
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Consts.METHOD_INJECT)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parameterSpec);

                TypeElement element = entry.getKey();
                List<Element> inner_elements = entry.getValue();
                String qualifiedName = element.getQualifiedName().toString(); // 全类名。
                String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                String className = element.getSimpleName() + Consts.NAME_OF_AUTOWIRED;

                logger.info(">>> Start process 111 " + inner_elements.size() + "field in " + element.getSimpleName());

                //生成类
                TypeSpec.Builder helper = TypeSpec.classBuilder(className)
                        .addSuperinterface(ClassName.get(type_ISyringe))
                        .addModifiers(Modifier.PUBLIC);

                //添加序列化field
                FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(type_JsonService.asType()), "serializationService", Modifier.PRIVATE).build();
                helper.addField(fieldSpec);

                //方法中对序列化初始化
                methodBuilder.addStatement("serializationService = $T.getInstance().navigation($T.class)"
                        , ARouterClass, ClassName.get(type_JsonService));
                methodBuilder.addStatement("$T substitute = ($T)target", ClassName.get(element), ClassName.get(element));

                //生成注解体
                for (Element childElement : inner_elements) {
                    Autowired annotation = childElement.getAnnotation(Autowired.class);

                    //处理provider变量
                    String simpleName = childElement.getSimpleName().toString();
                    if (types.isSubtype(childElement.asType(), typeProvider)) { //第一个是第二个的子类
                        if ("".equals(annotation.name())) {
                            methodBuilder.addStatement("substitute." + simpleName + "= $T.getInstance().navigation($T.class)"
                                    , ARouterClass, ClassName.get(childElement.asType()));
                        } else {  //这个name必须为provider的path路径
                            methodBuilder.addStatement("substitute." + simpleName + "=($T)$T.getInstance().build($S).navigation()"
                                    , ClassName.get(childElement.asType()), ARouterClass, annotation.name());
                        }
                    } else {
                        String originValue = "substitute." + simpleName;
                        String statement = "substitute." + simpleName + " = " + buildCastCode(childElement) + "substitute.";
                        Boolean isActivity = false;
                        if (types.isSubtype(element.asType(), typeActivity)) {
                            isActivity = true;
                            statement += "getIntent().";
                        } else if (types.isSubtype(element.asType(), typeFragment)) {
                            statement += "getArguments().";
                        }
                        statement = buildStatement(originValue, statement, typeUtils.typeExchange(childElement), isActivity, false);
                        if (statement.startsWith("serializationService.")) {
                            methodBuilder.beginControlFlow("if(null != serializationService)");
                            methodBuilder.addStatement("substitute." + simpleName + " = " + statement,
                                    (StringUtils.isEmpty(annotation.name()) ? simpleName : annotation.name()),
                                    ClassName.get(element.asType()));
                            methodBuilder.endControlFlow();
                        } else {
                            methodBuilder.addStatement(statement, StringUtils.isEmpty(annotation.name()) ? simpleName : annotation.name());
                        }
                    }
                }
                helper.addMethod(methodBuilder.build());
                try {
                    JavaFile.builder(packageName, helper.build()).build().writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            logger.info(">>>  AutoWired processor complete");
        }
    }

    private String buildStatement(String originalValue, String statement, int type, boolean isActivity, boolean isKt) {
        switch (TypeKind.values()[type]) {
            case BOOLEAN:
                statement += "getBoolean" + (isActivity ? "Extra" : "") + "($S, " + originalValue + ")";
                break;
            case BYTE:
                statement += "getByte" + (isActivity ? "Extra" : "") + "($S, " + originalValue + ")";
                break;
            case SHORT:
                statement += "getShort" + (isActivity ? "Extra" : "") + "($S, " + originalValue + ")";
                break;
            case INT:
                statement += "getInt" + (isActivity ? "Extra" : "") + "($S, " + originalValue + ")";
                break;
            case LONG:
                statement += "getLong" + (isActivity ? "Extra" : "") + "($S, " + originalValue + ")";
                break;
            case CHAR:
                statement += "getChar" + (isActivity ? "Extra" : "") + "($S, " + originalValue + ")";
                break;
            case FLOAT:
                statement += "getFloat" + (isActivity ? "Extra" : "") + "($S, " + originalValue + ")";
                break;
            case DOUBLE:
                statement += "getDouble" + (isActivity ? "Extra" : "") + "($S, " + originalValue + ")";
                break;
            case STRING:
                statement += (isActivity ? ("getExtras() == null ? " + originalValue + " : substitute.getIntent().getExtras().getString($S") : ("getString($S")) + ", " + originalValue + ")";
                break;
            case SERIALIZABLE:
                statement += (isActivity ? ("getSerializableExtra($S)") : ("getSerializable($S)"));
                break;
            case PARCELABLE:
                statement += (isActivity ? ("getParcelableExtra($S)") : ("getParcelable($S)"));
                break;
            case OBJECT:
                statement =
                        "serializationService.parseObject(substitute." + (isActivity ? "getIntent()." : "getArguments().") + (isActivity ? "getStringExtra($S)" : "getString($S)") + ",new " + Consts.TYPE_WRAPPER + "<$T>(){}.getType())";
                break;
        }
        return statement;
    }


    private String buildCastCode(Element element) {
        if (typeUtils.typeExchange(element) == TypeKind.SERIALIZABLE.ordinal()) {
            return CodeBlock.builder().add("($T)", ClassName.get(element.asType())).build().toString();
        }
        return "";
    }


    private void categories(Set<? extends Element> elementsAnnotatedWith) {
        if (CollectionUtils.isNotEmpty(elementsAnnotatedWith)) {
            for (final Element element : elementsAnnotatedWith) {
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement(); //父类
                if (element.getModifiers().contains(Modifier.PRIVATE)) {
                    throw new RuntimeException("field is private");
                }
                if (parentAndChild.containsKey(enclosingElement)) {
                    parentAndChild.get(enclosingElement).add(element);
                } else {
                    parentAndChild.put(enclosingElement, new ArrayList<Element>() {
                        {
                            add(element);
                        }
                    });
                }
            }
            logger.info("categories finished");
        }
    }
}
