package com.github.cstettler.dddttc.aspect;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static javax.lang.model.SourceVersion.RELEASE_8;

abstract class StereotypeBasedAspectAnnotationProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Set<String> processedAnnotatedElementNames;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotations().stream()
                .map((supportedAnnotation) -> supportedAnnotation.getName())
                .collect(toSet());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        this.filer = processingEnvironment.getFiler();
        this.messager = processingEnvironment.getMessager();
        this.processedAnnotatedElementNames = new HashSet<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        supportedAnnotations().forEach((supportedAnnotation) -> {
            roundEnvironment.getElementsAnnotatedWith(supportedAnnotation).stream()
                    .filter((annotatedElement) -> annotatedElement instanceof TypeElement)
                    .forEach((annotatedElement) -> {
                        TypeElement typeElement = (TypeElement) annotatedElement;

                        String fullyQualifiedName = fullyQualifiedElementName(typeElement);
                        String simpleName = simpleName(typeElement);

                        if (!this.processedAnnotatedElementNames.contains(fullyQualifiedName)) {
                            if (shouldWriteAspectFor(typeElement)) {
                                try {
                                    JavaFileObject aspectFile = this.filer.createSourceFile(aspectName(simpleName), typeElement);
                                    Writer writer = aspectFile.openWriter();

                                    PrintWriter printWriter = new PrintWriter(writer);
                                    writeAspectCode(printWriter, aspectName(simpleName), typeElement);

                                    printWriter.close();
                                } catch (IOException e) {
                                    this.messager.printMessage(Diagnostic.Kind.ERROR, "unable to " + aspectDescription() + " aspect for element '" + simpleName + "': " + e.getMessage());
                                }
                            }

                            this.processedAnnotatedElementNames.add(fullyQualifiedName);
                        }
                    });
        });

        return false;
    }

    protected String elementPackage(TypeElement typeElement) {
        String fullyQualifiedElementName = fullyQualifiedElementName(typeElement);
        String simpleName = simpleName(typeElement);

        return fullyQualifiedElementName.substring(0, fullyQualifiedElementName.length() - simpleName.length() - 1);
    }

    protected String fullyQualifiedElementName(TypeElement typeElement) {
        return typeElement.toString();
    }

    protected String simpleName(TypeElement typeElement) {
        return typeElement.getSimpleName().toString();
    }

    protected abstract Set<Class<? extends Annotation>> supportedAnnotations();

    protected abstract String aspectDescription();

    protected abstract String aspectName(String simpleName);

    protected abstract boolean shouldWriteAspectFor(TypeElement annotatedElement);

    protected abstract void writeAspectCode(PrintWriter writer, String aspectName, TypeElement typeElement);

    boolean hasDeclaredMethod(TypeElement typeElement, String name, Class<?>... parameterTypes) {
        return typeElement.getEnclosedElements().stream()
                .filter((element) -> element instanceof ExecutableElement)
                .anyMatch((methodElement) -> isMethod((ExecutableElement) methodElement, name, parameterTypes));
    }

    private static boolean isMethod(ExecutableElement executableElement, String name, Class<?>... parameterTypes) {
        return executableElement.getSimpleName().toString().equals(name)
                && parameterTypes(executableElement).size() == parameterTypes.length
                && parameterTypesMatch(parameterTypes(executableElement), parameterTypes);
    }

    private static boolean parameterTypesMatch(List<? extends TypeMirror> actualParameterTypes, Class<?>[] expectedParameterTypes) {
        for (int i = 0; i < expectedParameterTypes.length; i++) {
            DeclaredType parameterTypeMirror = (DeclaredType) actualParameterTypes.get(i);
            Class<?> parameterType = expectedParameterTypes[i];

            String actualParameterTypeName = parameterTypeMirror.toString();
            String expectedParameterTypeName = parameterType.getName();

            if (!(actualParameterTypeName.equals(expectedParameterTypeName))) {
                return false;
            }
        }

        return true;
    }

    private static List<? extends TypeMirror> parameterTypes(ExecutableElement executableElement) {
        return ((ExecutableType) executableElement.asType()).getParameterTypes();
    }

}
