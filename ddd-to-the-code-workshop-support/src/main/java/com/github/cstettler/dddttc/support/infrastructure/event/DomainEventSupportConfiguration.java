package com.github.cstettler.dddttc.support.infrastructure.event;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;
import java.util.List;

import static com.github.cstettler.dddttc.support.infrastructure.event.DomainEventTypeMappings.domainEventTypeMappings;

@EnableScheduling
public class DomainEventSupportConfiguration {

    @Bean
    PendingDomainEventStore pendingDomainEventStore(NamedParameterJdbcTemplate jdbcTemplate) {
        return new PendingDomainEventStore(jdbcTemplate);
    }

    @Bean
    PendingDomainEventPublisher pendingDomainEventDispatcher(PendingDomainEventStore pendingDomainEventStore, PlatformTransactionManager transactionManager, ConnectionFactory connectionFactory) {
        return new PendingDomainEventPublisher(pendingDomainEventStore, transactionManager, connectionFactory);
    }

    @Bean(destroyMethod = "stopMessageListenerContainers")
    DomainEventHandlerRegistrar domainEventHandlerRegistrar(DomainEventTypeResolver domainEventTypeResolver, DomainEventSerializer domainEventSerializer, ConnectionFactory connectionFactory, PlatformTransactionManager transactionManager) {
        return new DomainEventHandlerRegistrar(domainEventTypeResolver, domainEventSerializer, connectionFactory, transactionManager);
    }

    @Bean
    DomainEventTypeResolver domainEventTypeResolver(List<DomainEventTypeMappings> domainEventTypeMappings) {
        return new DomainEventTypeResolver(domainEventTypeMappings);
    }

    @Bean
    DomainEventSerializer domainEventSerializer() {
        return new DomainEventSerializer();
    }

    @Bean
    DomainEventTypeMappings defaultDomainEventTypeMappings() {
        return domainEventTypeMappings().build();
    }

    @Bean
    BeanPostProcessor domainEventHandlerAnnotationBeanPostProcessor(DomainEventHandlerRegistrar domainEventHandlerRegistrar) {
        return new DomainEventHandlerAnnotationBeanPostProcessor(domainEventHandlerRegistrar);
    }

}
