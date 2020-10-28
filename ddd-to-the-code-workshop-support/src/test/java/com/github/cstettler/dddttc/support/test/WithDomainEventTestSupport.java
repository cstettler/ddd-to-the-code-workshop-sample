package com.github.cstettler.dddttc.support.test;

import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;
import com.github.cstettler.dddttc.support.test.WithDomainEventTestSupport.DomainEventRecorderProvider;
import com.github.cstettler.dddttc.support.test.WithDomainEventTestSupport.TestDomainEventPublisherConfiguration;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;
import static org.springframework.test.context.junit.jupiter.SpringExtension.getApplicationContext;

@Target(TYPE)
@Retention(RUNTIME)
@ExtendWith(DomainEventRecorderProvider.class)
@Import(TestDomainEventPublisherConfiguration.class)
public @interface WithDomainEventTestSupport {

    class DomainEventRecorderProvider implements AfterEachCallback, ParameterResolver {

        @Override
        public void afterEach(ExtensionContext extensionContext) {
            domainEventRecorder(extensionContext).clearRecordedDomainEvents();
        }

        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return isAnnotated(extensionContext.getTestClass(), WithDomainEventTestSupport.class);
        }

        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return domainEventRecorder(extensionContext);
        }

        private static DomainEventRecorder domainEventRecorder(ExtensionContext extensionContext) {
            return getApplicationContext(extensionContext).getBean(DomainEventRecorder.class);
        }

    }


    class DomainEventRecorder {

        private final List<Object> recordedDomainEvents;

        DomainEventRecorder() {
            this.recordedDomainEvents = new ArrayList<>();
        }

        void recordDomainEvent(Object domainEvent) {
            this.recordedDomainEvents.add(domainEvent);
        }

        public int numberOfRecordedDomainEvents() {
            return this.recordedDomainEvents.size();
        }

        public <T> T singleRecordedDomainEvent() {
            return recordedDomainEvent(0);
        }

        @SuppressWarnings("unchecked")
        <T> T recordedDomainEvent(int index) {
            return (T) this.recordedDomainEvents.get(index);
        }

        void clearRecordedDomainEvents() {
            this.recordedDomainEvents.clear();
        }

    }


    class TestDomainEventPublisherConfiguration {

        @Bean
        DomainEventRecorder domainEventRecorder() {
            return new DomainEventRecorder();
        }

        @Bean
        BeanPostProcessor domainEventPublisherInterceptorBeanPostProcessor(DomainEventRecorder domainEventRecorder) {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                    if (bean instanceof DomainEventPublisher) {
                        DomainEventPublisher domainEventPublisher = (DomainEventPublisher) bean;

                        return (DomainEventPublisher) domainEvent -> {
                            domainEventPublisher.publish(domainEvent);
                            domainEventRecorder.recordDomainEvent(domainEvent);
                        };
                    }

                    return bean;
                }
            };
        }

    }

}
