package com.github.cstettler.dddttc.support.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ErrorHandler;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

class DomainEventHandlerRegistrar {

    private final DomainEventTypeResolver domainEventTypeResolver;
    private final DomainEventSerializer domainEventSerializer;
    private final ConnectionFactory connectionFactory;
    private final TransactionTemplate transactionTemplate;
    private final List<MessageListenerContainer> messageListenerContainers;

    DomainEventHandlerRegistrar(DomainEventTypeResolver domainEventTypeResolver, DomainEventSerializer domainEventSerializer, ConnectionFactory connectionFactory, PlatformTransactionManager transactionManager) {
        this.domainEventTypeResolver = domainEventTypeResolver;
        this.domainEventSerializer = domainEventSerializer;
        this.connectionFactory = connectionFactory;
        this.transactionTemplate = requiresNewTransactionTemplate(transactionManager);
        this.messageListenerContainers = new ArrayList<>();
    }

    void registerDomainEventHandler(Object target, Method method) {
        MessageListenerContainer messageListenerContainer = messageListenerContainerFor(new DomainEventHandlerMethod(target, method));
        this.messageListenerContainers.add(messageListenerContainer);

        messageListenerContainer.start();
    }

    void stopMessageListenerContainers() {
        this.messageListenerContainers.forEach((messageListenerContainer) -> messageListenerContainer.stop());
    }

    private MessageListenerContainer messageListenerContainerFor(DomainEventHandlerMethod domainEventHandlerMethod) {
        String destinationName = this.domainEventTypeResolver.resolveDomainEventType(domainEventHandlerMethod.domainEventParameterType());

        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.connectionFactory);
        messageListenerContainer.setDestinationName(destinationName);
        messageListenerContainer.setPubSubDomain(true);
        messageListenerContainer.setClientId(domainEventHandlerMethod.id());
        messageListenerContainer.setSubscriptionName(domainEventHandlerMethod.id());
        messageListenerContainer.setMessageListener(new DomainEventHandlerMethodDispatchingMessageListener(destinationName, domainEventHandlerMethod, this.domainEventSerializer, this.transactionTemplate));
        messageListenerContainer.setErrorHandler(new LoggingAndRethrowingErrorHandler());
        messageListenerContainer.afterPropertiesSet();

        return messageListenerContainer;
    }

    private static TransactionTemplate requiresNewTransactionTemplate(PlatformTransactionManager platformTransactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);

        return transactionTemplate;
    }


    static class DomainEventHandlerMethod {

        private final Object target;
        private final Method method;

        DomainEventHandlerMethod(Object target, Method method) {
            this.target = target;
            this.method = method;
        }

        String id() {
            return this.method.getDeclaringClass().getName() + "." + this.method.getName();
        }

        Class<?> domainEventParameterType() {
            return this.method.getParameterTypes()[0];
        }

        void invoke(Object domainEvent) {
            boolean accessible = this.method.isAccessible();

            try {
                this.method.setAccessible(true);
                this.method.invoke(this.target, domainEvent);
            } catch (Exception e) {
                throw new IllegalStateException("invocation of domain handler method '" + this.method + "' failed", e);
            } finally {
                this.method.setAccessible(accessible);
            }

        }

    }


    private static class LoggingAndRethrowingErrorHandler implements ErrorHandler {

        private static Logger LOGGER = LoggerFactory.getLogger(LoggingAndRethrowingErrorHandler.class);

        @Override
        public void handleError(Throwable t) {
            LOGGER.error("error while processing incoming message", t);

            throw new IllegalStateException("error while processing incoming message", t);
        }
    }


    private static class DomainEventHandlerMethodDispatchingMessageListener implements MessageListener {

        private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventHandlerMethodDispatchingMessageListener.class);

        private final String destinationName;
        private final DomainEventHandlerMethod domainEventHandlerMethod;
        private final DomainEventSerializer domainEventSerializer;
        private final TransactionTemplate transactionTemplate;

        DomainEventHandlerMethodDispatchingMessageListener(String destinationName, DomainEventHandlerMethod domainEventHandlerMethod, DomainEventSerializer domainEventSerializer, TransactionTemplate transactionTemplate) {
            this.destinationName = destinationName;
            this.domainEventHandlerMethod = domainEventHandlerMethod;
            this.domainEventSerializer = domainEventSerializer;
            this.transactionTemplate = transactionTemplate;
        }


        @Override
        public void onMessage(Message message) {
            this.transactionTemplate.execute((status) -> {
                if (!(message instanceof TextMessage)) {
                    throw new IllegalStateException("unsupported message '" + message + "' (must be text message)");
                }

                try {
                    TextMessage textMessage = (TextMessage) message;
                    String id = message.getStringProperty("domain-event-id");
                    String payload = textMessage.getText();
                    Class<?> domainEventParameterType = this.domainEventHandlerMethod.domainEventParameterType();

                    LOGGER.info("received message for domain event '" + id + "' on '" + this.destinationName + "' (payload '" + payload + "')");

                    Object domainEvent = this.domainEventSerializer.deserialize(payload, domainEventParameterType);

                    LOGGER.info("dispatching domain event '" + domainEvent + "' to domain event handler '" + this.domainEventHandlerMethod.id() + "'");

                    this.domainEventHandlerMethod.invoke(domainEvent);

                    return null;
                } catch (Exception e) {
                    throw new IllegalStateException("unable to dispatch message '" + message + "' to domain event handler '" + this.domainEventHandlerMethod + "'", e);
                }
            });
        }
    }

}
