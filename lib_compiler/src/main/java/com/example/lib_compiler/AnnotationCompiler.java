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
        Set<? extends Element> elementsAnnotatedWith  = roundEnv.getElementsAnnotatedWith(Path.class);

        Map<String, String> map = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            Path annotation = typeElement.getAnnotation(Path.class);

            String key = annotation.path();
            String activityName = typeElement.getQualifiedName().toString();
            map.put(key, activityName);
        }

        if (!map.isEmpty()) {
            Writer writer = null;
            String utilsName = "ARouterInjectUtils"+System.currentTimeMillis();
            try {
                JavaFileObject javaFileObject = filer.createSourceFile("com.example.utils." + utilsName);
                writer = javaFileObject.openWriter();
                writer.write("package com.example.utils;\n" +
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
