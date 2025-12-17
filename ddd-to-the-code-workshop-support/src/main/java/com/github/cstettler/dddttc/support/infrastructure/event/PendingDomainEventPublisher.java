package com.github.cstettler.dddttc.support.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import java.util.List;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

class PendingDomainEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PendingDomainEventPublisher.class);

    private final PendingDomainEventStore pendingDomainEventStore;
    private final TransactionTemplate transactionTemplate;
    private final JmsTemplate jmsTemplate;

    PendingDomainEventPublisher(PendingDomainEventStore pendingDomainEventStore, PlatformTransactionManager transactionManager, ConnectionFactory connectionFactory) {
        this.pendingDomainEventStore = pendingDomainEventStore;
        this.transactionTemplate = requiresNewTransactionTemplate(transactionManager);
        this.jmsTemplate = topicBasedJmsTemplate(connectionFactory);
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    void triggerDomainEventDispatching() {
        loadNextPendingDomainEvents(10).forEach((pendingDomainEvent) -> {
            sendPendingDomainEvent(pendingDomainEvent);
            markPendingDomainEventDispatched(pendingDomainEvent);
        });
    }

    private List<PendingDomainEvent> loadNextPendingDomainEvents(int count) {
        return this.transactionTemplate.execute((status) -> this.pendingDomainEventStore.loadNextPendingDomainEvents(count));
    }

    private void markPendingDomainEventDispatched(PendingDomainEvent pendingDomainEvent) {
        this.transactionTemplate.execute((status) -> {
            this.pendingDomainEventStore.markPendingDomainEventDispatched(pendingDomainEvent);

            return null;
        });
    }

    private void sendPendingDomainEvent(PendingDomainEvent pendingDomainEvent) {
        this.jmsTemplate.send(pendingDomainEvent.type(), (session) -> {
            TextMessage textMessage = session.createTextMessage();
            textMessage.setStringProperty("domain-event-id", pendingDomainEvent.id());
            textMessage.setText(pendingDomainEvent.payload());

            return textMessage;
        });

        LOGGER.info("sent message for domain event '" + pendingDomainEvent.id() + "' to '" + pendingDomainEvent.type() + "' (payload '" + pendingDomainEvent.payload() + "'");
    }

    private static TransactionTemplate requiresNewTransactionTemplate(PlatformTransactionManager platformTransactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);

        return transactionTemplate;
    }

    private static JmsTemplate topicBasedJmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setPubSubDomain(true);

        return jmsTemplate;
    }

}
