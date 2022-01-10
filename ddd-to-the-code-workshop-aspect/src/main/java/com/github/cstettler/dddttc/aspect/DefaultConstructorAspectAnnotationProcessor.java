package com.github.cstettler.dddttc.aspect;

import com.github.cstettler.dddttc.stereotype.Aggregate;
import com.github.cstettler.dddttc.stereotype.DomainEvent;
import com.github.cstettler.dddttc.stereotype.ValueObject;
import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;
import javax.lang.model.element.TypeElement;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@AutoService(Processor.class)
public class DefaultConstructorAspectAnnotationProcessor extends StereotypeBasedAspectAnnotationProcessor {

    @Override
    protected Set<Class<? extends Annotation>> supportedAnnotations() {
        return new HashSet<>(asList(
                Aggregate.class,
                ValueObject.class,
                DomainEvent.class
        ));
    }

    @Override
    protected String aspectDescription() {
        return "default constructor";
    }

    @Override
    protected String aspectName(String simpleName) {
        return simpleName + "ProtectedDefaultConstructorAspect";
    }

    @Override
    protected boolean shouldWriteAspectFor(TypeElement annotatedElement) {
        return !(hasDeclaredMethod(annotatedElement, "<init>"));
    }

    @Override
    protected void writeAspectCode(PrintWriter writer, String aspectName, TypeElement typeElement) {
        writer.println("package " + elementPackage(typeElement) + ";");
        writer.println("");
        writer.println("import " + fullyQualifiedElementName(typeElement) + ";");
        writer.println("");
        writer.println("public aspect " + aspectName + " {");
        writer.println("");
        writer.println("    public " + simpleName(typeElement) + ".new() {");
        writer.println("        // e.g. for jpa or jackson deserializer");
        writer.println("    }");
        writer.println("");
        writer.println("}");
    }

}
