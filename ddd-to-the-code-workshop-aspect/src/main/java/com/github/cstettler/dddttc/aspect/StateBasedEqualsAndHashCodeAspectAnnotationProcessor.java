package com.github.cstettler.dddttc.aspect;

import com.github.cstettler.dddttc.stereotype.ValueObject;
import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;
import javax.lang.model.element.TypeElement;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Set;

import static java.util.Collections.singleton;

@AutoService(Processor.class)
public class StateBasedEqualsAndHashCodeAspectAnnotationProcessor extends StereotypeBasedAspectAnnotationProcessor {

    @Override
    protected String aspectDescription() {
        return "state-based equals / hash code";
    }

    @Override
    protected Set<Class<? extends Annotation>> supportedAnnotations() {
        return singleton(ValueObject.class);
    }

    @Override
    protected String aspectName(String simpleName) {
        return simpleName + "StateBasedEqualsAndHashCodeAspect";
    }

    @Override
    protected boolean shouldWriteAspectFor(TypeElement annotatedElement) {
        return !hasDeclaredMethod(annotatedElement, "equals", Object.class);
    }

    @Override
    protected void writeAspectCode(PrintWriter writer, String aspectName, TypeElement typeElement) {
        writer.println("package " + elementPackage(typeElement) + ";");
        writer.println("");
        writer.println("import " + fullyQualifiedElementName(typeElement) + ";");
        writer.println("import com.github.cstettler.dddttc.aspect.StateBasedEqualsAndHashCode;");
        writer.println("");
        writer.println("public aspect " + aspectName + " {");
        writer.println("");
        writer.println("    public boolean " + simpleName(typeElement) + ".equals(Object other) {");
        writer.println("        return StateBasedEqualsAndHashCode.stateBasedEquals(this, other);");
        writer.println("    }");
        writer.println("");
        writer.println("    public int " + simpleName(typeElement) + ".hashCode() {");
        writer.println("        return StateBasedEqualsAndHashCode.stateBasedHashCode(this);");
        writer.println("    }");
        writer.println("");
        writer.println("}");
    }

}
