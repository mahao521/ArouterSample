package com.example.mahao_compiler;

import com.example.mahao_annotation.annotation.Route;
import com.example.mahao_annotation.annotation.RouteType;
import com.example.mahao_annotation.model.RouteMeta;
import com.example.mahao_compiler.utils.Consts;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.management.MonitorInfo;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import sun.swing.StringUIClientPropertyKey;

@AutoService({Processor.class})
//@SupportedAnnotationTypes({Consts.ANNOTATION_ROUTE, Consts.ANNOTATION_AUTOWIRED})
public class RouterProcessor extends BaseProcessor {

    private Map<String, Set<RouteMeta>> groupMap = new HashMap<>();
    private Map<String, String> rootMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<String>() {
            {
                add(Consts.ANNOTATION_ROUTE);
                add(Consts.ANNOTATION_AUTOWIRED);
            }
        };
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Route.class);
            try {

                parSeElements(elementsAnnotatedWith);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void parSeElements(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isNotEmpty(routeElements)) {
            TypeMirror typeActivity = elementUtils.getTypeElement(Consts.ACTIVITY).asType();
            TypeMirror typeService = elementUtils.getTypeElement(Consts.SERVICE).asType();
            TypeMirror typeProvider = elementUtils.getTypeElement(Consts.PROVIDER).asType();
            //interface
            TypeElement typeGroup = elementUtils.getTypeElement(Consts.GROUP);
            TypeElement typeRoot = elementUtils.getTypeElement(Consts.ROOT);



            TypeElement typeProviderGroup = elementUtils.getTypeElement(Consts.PROVIDER_GROUP);
            logger.info(">>>> className 1223 " + typeProviderGroup);
            ClassName routeMetaCn = ClassName.get(RouteMeta.class);
            ClassName routeTypeCn = ClassName.get(RouteType.class);

            //Map<String,Class<? extends IRouteGroup>>
            ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
                    ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(typeGroup))));

            //Map<String,RouteMeta>>
            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
                    ClassName.get(RouteMeta.class));

            //build input param name
            ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routers").build();
            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();
            ParameterSpec providerParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "providers").build();

            //build method loadInto
            MethodSpec.Builder loadIntoMethodRootBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(rootParamSpec);

            for (Element element : routeElements) {
                TypeMirror tm = element.asType();
                Route route = element.getAnnotation(Route.class);
                RouteMeta routeMeta = null;
                if (types.isSubtype(tm, typeActivity)) {
                    routeMeta = new RouteMeta(route, element, RouteType.ACTIVITY);
                } else if (types.isSubtype(tm, typeProvider)) {
                    routeMeta = new RouteMeta(route, element, RouteType.PROVIDER);
                } else if (types.isSubtype(tm, typeService)) {
                    routeMeta = new RouteMeta(route, element, RouteType.SERVICE);
                }
                categories(routeMeta);
            }
            //继续创建group的方法
            MethodSpec.Builder loadIntoMethodofProviderBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(providerParamSpec);
            for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
                String groupName = entry.getKey();
                MethodSpec.Builder loadInfoMethodOfGroupBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(groupParamSpec);
                Set<RouteMeta> groupMeta = entry.getValue();
                for (RouteMeta routeMeta : groupMeta) {
                    ClassName className = ClassName.get((TypeElement) routeMeta.getRawType());
                    logger.info(">>>> className " + className);

                    switch (routeMeta.getType()) {
                        case PROVIDER:
                            TypeElement rawType = (TypeElement) routeMeta.getRawType();
                            List<? extends TypeMirror> interfaces = rawType.getInterfaces();
                            for (TypeMirror tm : interfaces) {
                                if (types.isSameType(tm, typeProvider)) {
                                    loadIntoMethodofProviderBuilder.addStatement("providers.put($S,$T.build($T." + routeMeta.getType() + ",$T.class,$S,$S," + routeMeta.getPriority() + "))",
                                            routeMeta.getRawType().toString(),
                                            routeMetaCn,
                                            routeMetaCn,
                                            className,
                                            routeMeta.getPath(),
                                            routeMeta.getGroup());
                                } else if (types.isSubtype(tm, typeProvider)) {
                                    loadIntoMethodofProviderBuilder.addStatement("providers.put($S,$T.build($T." + routeMeta.getType() + ",$T.class,$S,$S," + routeMeta.getPriority() + "))",
                                            tm.toString(),
                                            routeMetaCn,
                                            routeTypeCn,
                                            className,
                                            routeMeta.getPath(),
                                            routeMeta.getGroup());
                                }
                            }
                            break;
                        default:
                            break;

                    }

                    //创建group
                    loadInfoMethodOfGroupBuilder.addStatement("atlas.put($S,$T.build($T." + routeMeta.getType() + ",$T.class,$S,$S," + routeMeta.getPriority() + "))",
                            routeMeta.getPath(),
                            routeMetaCn,
                            routeTypeCn,
                            className,
                            routeMeta.getPath().toLowerCase(),
                            routeMeta.getGroup().toLowerCase());
                }
                //生成groups
                String groupFileName = Consts.NAME_OF_GROUP + groupName;
                JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE, TypeSpec.classBuilder(groupFileName)
                        .addSuperinterface(ClassName.get(typeGroup))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(loadInfoMethodOfGroupBuilder.build())
                        .build()).build().writeTo(filer);
                rootMap.put(groupName, groupFileName);
            }
            if (MapUtils.isNotEmpty(rootMap)) {
                for (Map.Entry<String, String> entry : rootMap.entrySet()) {
                    loadIntoMethodRootBuilder.addStatement("routers.put($S,$T.class)", entry.getKey(), ClassName.get(Consts.PACKAGE_OF_GENERATE_FILE, entry.getValue()));
                }
            }
            //生成provider
            String providerMapFileName = Consts.NAME_OF_PROVIDER + Consts.SEPARATOR + moudleName;
            JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE, TypeSpec.classBuilder(providerMapFileName)
                    .addSuperinterface(ClassName.get(typeProviderGroup))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(loadIntoMethodofProviderBuilder.build())
                    .build()).build().writeTo(filer);
            //生成rooter
            String rootFileName = Consts.NAME_OF_ROOT + Consts.SEPARATOR + moudleName;
            JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE, TypeSpec.classBuilder(rootFileName)
                    .addSuperinterface(ClassName.get(typeRoot))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(loadIntoMethodRootBuilder.build())
                    .build()).build().writeTo(filer);
        }
    }

    private void categories(RouteMeta routeMeta) {
        if (routeMeta == null) return;
        if (routeVerify(routeMeta)) {
            Set<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                Set<RouteMeta> newRouteMeta = new TreeSet<>(new Comparator<RouteMeta>() { //去重。
                    @Override
                    public int compare(RouteMeta o1, RouteMeta o2) {
                        try {
                            return o1.getPath().compareTo(o2.getPath());
                        } catch (NullPointerException e) {
                            return 0;
                        }
                    }
                });
                newRouteMeta.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), newRouteMeta);
            } else {
                routeMetas.add(routeMeta);
            }
        }
    }

    /**
     * verify the route meta
     *
     * @param meta
     * @return
     */
    private boolean routeVerify(RouteMeta meta) {
        String path = meta.getPath();
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }
        if (StringUtils.isEmpty(meta.getGroup())) {
            try {
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }
                meta.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}














