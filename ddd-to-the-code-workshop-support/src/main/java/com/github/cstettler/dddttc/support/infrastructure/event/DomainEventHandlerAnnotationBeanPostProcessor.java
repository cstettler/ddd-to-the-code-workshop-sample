package com.github.cstettler.dddttc.support.infrastructure.event;

import com.github.cstettler.dddttc.stereotype.DomainEvent;
import com.github.cstettler.dddttc.stereotype.DomainEventHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

import static java.util.Arrays.stream;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

class DomainEventHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final DomainEventHandlerRegistrar domainEventHandlerRegistrar;

    DomainEventHandlerAnnotationBeanPostProcessor(DomainEventHandlerRegistrar domainEventHandlerRegistrar) {
        this.domainEventHandlerRegistrar = domainEventHandlerRegistrar;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        stream(bean.getClass().getDeclaredMethods())
                .filter((method) -> method.isAnnotationPresent(DomainEventHandler.class))
                .peek((method) -> validateDomainEventHandlerSignature(method))
                .forEach((domainEventHandlerMethod) -> this.domainEventHandlerRegistrar.registerDomainEventHandler(bean, domainEventHandlerMethod));

        return bean;
    }

    private void validateDomainEventHandlerSignature(Method method) {
        if (!(method.getReturnType().equals(Void.TYPE))) {
            throw new IllegalStateException("domain event handler method '" + method + "' must be void");
        }

        if (method.getParameterCount() != 1) {
            throw new IllegalStateException("domain event handler method '" + method + "' must declare one single domain event parameter");
        }

        if (domainEventAnnotation(method.getParameterTypes()[0]) == null) {
            throw new IllegalStateException("parameter of domain handler method '" + method + "' is not annotated with '@DomainEvent'");
        }
    }

    private static DomainEvent domainEventAnnotation(Class<?> parameterType) {
        return findAnnotation(parameterType, DomainEvent.class);
    }

}
