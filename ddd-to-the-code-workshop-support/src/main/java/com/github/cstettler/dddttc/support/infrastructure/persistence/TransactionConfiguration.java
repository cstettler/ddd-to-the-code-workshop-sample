package com.github.cstettler.dddttc.support.infrastructure.persistence;

import com.github.cstettler.dddttc.stereotype.ApplicationService;
import com.github.cstettler.dddttc.stereotype.InfrastructureService;
import com.github.cstettler.dddttc.stereotype.Repository;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_MANDATORY;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;
import static org.springframework.util.ReflectionUtils.isEqualsMethod;
import static org.springframework.util.ReflectionUtils.isHashCodeMethod;

public class TransactionConfiguration {

    @Bean
    BeanFactoryTransactionAttributeSourceAdvisor applicationServiceTransactionAttributeSourceAdvisor(BeanFactory beanFactory) {
        return stereotypeBasedTransactionAttributeSourceAdvisor(beanFactory, ApplicationService.class, PROPAGATION_REQUIRED);
    }

    @Bean
    BeanFactoryTransactionAttributeSourceAdvisor repositoryTransactionAttributeSourceAdvisor(BeanFactory beanFactory) {
        return stereotypeBasedTransactionAttributeSourceAdvisor(beanFactory, Repository.class, PROPAGATION_MANDATORY);
    }

    @Bean
    BeanFactoryTransactionAttributeSourceAdvisor infrastructureServiceTransactionAttributeSourceAdvisor(BeanFactory beanFactory) {
        return stereotypeBasedTransactionAttributeSourceAdvisor(beanFactory, InfrastructureService.class, PROPAGATION_MANDATORY);
    }

    private static BeanFactoryTransactionAttributeSourceAdvisor stereotypeBasedTransactionAttributeSourceAdvisor(BeanFactory beanFactory, Class<? extends Annotation> stereotype, int propagationBehavior) {
        BeanFactoryTransactionAttributeSourceAdvisor transactionAttributeSourceAdvisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        transactionAttributeSourceAdvisor.setClassFilter(new AnnotationClassFilter(stereotype, true));
        transactionAttributeSourceAdvisor.setAdvice(stereotypeBasedTransactionInterceptor(beanFactory, stereotype, propagationBehavior));

        return transactionAttributeSourceAdvisor;
    }

    private static TransactionInterceptor stereotypeBasedTransactionInterceptor(BeanFactory beanFactory, Class<? extends Annotation> stereotype, int propagationBehavior) {
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionAttributeSource(stereotypeBasedTransactionAttributeSource(stereotype, propagationBehavior));
        transactionInterceptor.setBeanFactory(beanFactory);

        return transactionInterceptor;
    }

    private static TransactionAttributeSource stereotypeBasedTransactionAttributeSource(Class<? extends Annotation> stereotype, int propagationBehavior) {
        return (method, targetClassCandidate) -> {
            if (isEqualsMethod(method) || isHashCodeMethod(method)) {
                return null;
            }

            if (findAnnotation(targetClass(method, targetClassCandidate), stereotype) != null && isPublic(method.getModifiers())) {
                DefaultTransactionAttribute defaultTransactionAttribute = new DefaultTransactionAttribute();
                defaultTransactionAttribute.setPropagationBehavior(propagationBehavior);

                return defaultTransactionAttribute;
            }

            return null;
        };
    }

    private static Class<?> targetClass(Method method, Class<?> targetClassCandidate) {
        if (targetClassCandidate != null) {
            return targetClassCandidate;
        }

        return method.getDeclaringClass();
    }

}
