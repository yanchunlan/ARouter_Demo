package com.example.lib_compiler;

import com.example.lib_annotation.Path;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * author:  ycl
 * date:  2019/09/24 15:05
 * desc:
 */
@AutoService(Processor.class)
public class AnnotationCompiler extends AbstractProcessor {

    private Filer filer;// 文件对象


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    /**
     *     声明返回要处理哪个注解
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Path.class.getCanonicalName());
        return types;
    }

    /**
     * 持java版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


    /**
     * 核心
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        /*
        拿到该模块所有path注解的节点
         */
        Set<? extends Element> elementsAnnotatedWith  = roundEnv.getElementsAnnotatedWith(Path.class);

        /*
         结构化数据
          */
        Map<String, String> map = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            Path annotation = typeElement.getAnnotation(Path.class);

            /*
             读取值
              */
            String key = annotation.path();
            /*
             包名+类名
              */
            String activityName = typeElement.getQualifiedName().toString();
            map.put(key, activityName);
        }

        if (!map.isEmpty()) {
            /*
             写文件
              */
            Writer writer = null;
            String utilsName = "ActivityUtils";
            try {
                JavaFileObject javaFileObject = filer.createSourceFile("com.example.arouter_demo.utils." + utilsName);
                writer = javaFileObject.openWriter();
                writer.write("package com.example.arouter_demo.utils;\n" +
                        "\n"
                        + "import com.example.lib_core.ARouter;\n"
                        + "import com.example.lib_core.IRoute;\n"
                        + "\n"
                        + "public class " + utilsName + " implements IRoute {\n"
                        + "\n" +
                        " @Override\n" +
                        " public void putActivity() {"
                        + "\n");
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String path = iterator.next();
                    String value = map.get(path);
                    writer.write("ARouter.getInstance().putActivity(\"" + path + "\","
                            + value + ".class);\n");
                }
                writer.write("}\n}");
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
