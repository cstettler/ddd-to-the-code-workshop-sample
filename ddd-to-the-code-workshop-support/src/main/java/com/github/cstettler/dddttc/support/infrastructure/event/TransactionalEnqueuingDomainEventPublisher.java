package com.github.cstettler.dddttc.support.infrastructure.event;

import com.github.cstettler.dddttc.stereotype.DomainEvent;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive;

class TransactionalEnqueuingDomainEventPublisher implements DomainEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalEnqueuingDomainEventPublisher.class);

    private final PendingDomainEventStore pendingDomainEventStore;
    private final DomainEventTypeResolver domainEventTypeResolver;
    private final DomainEventSerializer domainEventSerializer;

    TransactionalEnqueuingDomainEventPublisher(PendingDomainEventStore pendingDomainEventStore, DomainEventTypeResolver domainEventTypeResolver, DomainEventSerializer domainEventSerializer) {
        this.pendingDomainEventStore = pendingDomainEventStore;
        this.domainEventTypeResolver = domainEventTypeResolver;
        this.domainEventSerializer = domainEventSerializer;
    }

    @Override
    public void publish(Object domainEvent) {
        if (domainEventAnnotation(domainEvent.getClass()) == null) {
            throw new IllegalStateException("published domain event '" + domainEvent + "' is not annotated with @DomainEvent");
        }

        if (!(isActualTransactionActive())) {
            throw new IllegalStateException("no transaction active when trying to enqueue domain event '" + domainEvent + "'");
        }

        String id = randomUUID().toString();
        String type = this.domainEventTypeResolver.resolveDomainEventType(domainEvent.getClass());
        String payload = this.domainEventSerializer.serialize(domainEvent);
        LocalDateTime publishedAt = now();

        PendingDomainEvent pendingDomainEvent = new PendingDomainEvent(id, type, payload, publishedAt);
        this.pendingDomainEventStore.storePendingDomainEvent(pendingDomainEvent);

        LOGGER.info("enqueued domain event '" + id + "' for '" + type + "' (payload '" + payload + "'");
    }

    private static DomainEvent domainEventAnnotation(Class<?> domainEventType) {
        return findAnnotation(domainEventType, DomainEvent.class);
    }

}
