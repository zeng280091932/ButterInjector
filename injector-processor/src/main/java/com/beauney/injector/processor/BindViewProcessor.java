package com.beauney.injector.processor;

import com.beauney.injector.annotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * @author zengjiantao
 * @since 2020-08-20
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    private Elements elementUtils;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        FileUtils.print("Start process------------->");
        Map<TypeElement, List<FieldViewBinding>> targetMap = new HashMap<>();
        FileUtils.print("Start process------------->");
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            FileUtils.print("element:" + element.getSimpleName());

            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            List<FieldViewBinding> list = targetMap.get(enclosingElement);
            if (list == null) {
                list = new ArrayList<>();
                targetMap.put(enclosingElement, list);
            }

            int id = element.getAnnotation(BindView.class).value();
            String fieldName = element.getSimpleName().toString();
            TypeMirror typeMirror = element.asType();

            FieldViewBinding fieldViewBinding = new FieldViewBinding(fieldName, typeMirror, id);
            list.add(fieldViewBinding);
        }

        for (Map.Entry<TypeElement, List<FieldViewBinding>> item : targetMap.entrySet()) {
            List<FieldViewBinding> list = item.getValue();
            if (list == null || list.size() == 0) {
                continue;
            }
            TypeElement enclosingElement = item.getKey();
            String packageName = getPackageName(enclosingElement);
            String activityClassName = getClassName(enclosingElement, packageName);
            ClassName className = ClassName.bestGuess(activityClassName);
            ClassName viewBinder = ClassName.get("com.beauney.injector.library", "ViewBinder");

            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(activityClassName + "$$ViewBinder")
                    .addModifiers(Modifier.PUBLIC)
                    .addTypeVariable(TypeVariableName.get("T", className))
                    .addSuperinterface(ParameterizedTypeName.get(viewBinder, className));

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addAnnotation(Override.class)
                    .addParameter(className, "target", Modifier.FINAL);

            for (int i = 0; i < list.size(); i++) {
                FieldViewBinding fieldViewBinding = list.get(i);
                String classNameString = fieldViewBinding.getType().toString();
                ClassName viewClass = ClassName.bestGuess(classNameString);

                methodBuilder.addStatement("target.$L = ($T)target.findViewById($L)",
                        fieldViewBinding.getName(), viewClass, fieldViewBinding.getResId());
            }

            typeBuilder.addMethod(methodBuilder.build());

            try {
                JavaFile.builder(packageName, typeBuilder.build())
                        .addFileComment("auto create make")
                        .build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private String getClassName(TypeElement enclosingElement, String packageName) {
        int packageLength = packageName.length() + 1;
        return enclosingElement.getQualifiedName().toString().substring(packageLength).replace(".", "$");
    }

    private String getPackageName(TypeElement enclosingElement) {
        return elementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
    }
}
