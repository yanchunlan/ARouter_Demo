package com.example.lib_compiler;

import com.example.lib_annotation.Path;
import com.example.lib_compiler.utils.RouterConstants;
import com.example.lib_compiler.utils.RouterLogger;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * author:  ycl
 * date:  2019/09/24 15:05
 * desc:
 */
@AutoService(Processor.class)
public class RouteProcessor extends AbstractProcessor {

    private String mModuleName;
    private RouterLogger mRouterLogger;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mRouterLogger = new RouterLogger(processingEnv.getMessager());
        /*
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [moduleName: project.getName()]
            }
        }*/
        Map<String, String> options = processingEnv.getOptions();
        if (options != null && !options.isEmpty()) {
            mModuleName = options.get(RouterConstants.OPTION_MODULE_NAME);
            mRouterLogger.setOpenLog("true".equals(options.get(RouterConstants.OPTION_OPEN_LOG)));
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Path.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Path.class);
        if (elements == null || elements.isEmpty()) {
            return true;
        }

        // > Task :module1:javaPreCompileDebug
        mRouterLogger.info(String.format("> %s : %s :  begin", RouteProcessor.class.getSimpleName(), mModuleName));

        Map<String, ClassName> map = new HashMap<>();
        for (Element element : elements) {
            if (!element.getKind().isClass()) continue;
            TypeElement typeElement = (TypeElement) element;
            if (!validateClass(typeElement)) continue;

            Path annotation = typeElement.getAnnotation(Path.class);
            String[] paths = annotation.value();
            ClassName className = ClassName.get(typeElement);
            mRouterLogger.info(String.format("> %s : %s :  found routed target: %s", RouteProcessor.class.getSimpleName(), mModuleName,
                    typeElement.getQualifiedName()));
            for (String path : paths) {
                if (map.containsKey(path)) {
                    throw new RuntimeException(String.format("> %s : %s : repeated : %s[%s, %s]", RouteProcessor.class.getSimpleName(), mModuleName,
                            path, typeElement.getQualifiedName(), map.get(path)));
                }
                map.put(path, className);
            }
        }
        if (map.isEmpty()) return true;
        if (mModuleName == null) {
            throw new RuntimeException(String.format("> %s : %s : mModuleName == null", RouteProcessor.class.getSimpleName(), mModuleName,
                    RouterConstants.OPTION_MODULE_NAME));
        }
        String validModuleName = mModuleName.replace(".", "_").replace("-", "_");


        // javaPoet  start
        MethodSpec.Builder methodPutActivity = MethodSpec.methodBuilder(RouterConstants.METHOD_PUT_ACTIVITY)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        // not exist
        ClassName ARouter = ClassName.get("com.example.lib_core", "ARouter");

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            ClassName className = map.get(path);
            // $S  string  $T type.class
            // auto import
            methodPutActivity.addStatement("$T.getInstance().putActivity($S, $T.class)", ARouter, path, className);
        }

        TypeElement interfaceType = processingEnv.getElementUtils().getTypeElement(RouterConstants.IROUTE_FULL_NAME);
        String className = capitalize(validModuleName) + RouterConstants.IROUTE;
        TypeSpec type = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodPutActivity.build())
                .addJavadoc(RouterConstants.CLASS_JAVA_DOC)
                .build();
        try {
            mRouterLogger.info(String.format("> %s : %s :  routed create: %s.%s", RouteProcessor.class.getSimpleName(), mModuleName,
                    RouterConstants.APT_PACKAGE_NAME, className));
            JavaFile.builder(RouterConstants.APT_PACKAGE_NAME, type).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRouterLogger.info(String.format("> %s : %s :  end", RouteProcessor.class.getSimpleName(), mModuleName));

        return true;
    }

    private String capitalize(CharSequence self) {
        return self.length() == 0 ? "" :
                "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }

    private boolean validateClass(TypeElement typeElement) {
        // not activity/fragment
        if (!isSubType(typeElement, RouterConstants.ACTIVITY_FULL_NAME) && !isSubType(typeElement, RouterConstants.FRAGMENT_X_FULL_NAME)) {
            mRouterLogger.error(typeElement, String.format("> %s : %s :  %s is activity/fragment", RouteProcessor.class.getSimpleName(), mModuleName,
                    typeElement.getSimpleName().toString()));
            return false;
        }
        //  abstract class
        Set<Modifier> modifiers = typeElement.getModifiers();
        if (modifiers.contains(Modifier.ABSTRACT)) {
            mRouterLogger.error(typeElement, String.format("> %s : %s :  %s is abstract", RouteProcessor.class.getSimpleName(), mModuleName,
                    (typeElement).getQualifiedName()));
            return false;
        }
        return true;
    }

    private boolean isSubType(Element typeElement, String type) {
        return processingEnv.getTypeUtils().isSubtype(typeElement.asType(),
                processingEnv.getElementUtils().getTypeElement(type).asType());
    }
}
