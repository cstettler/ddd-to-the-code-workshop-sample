package com.github.cstettler.dddttc.support.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ErrorHandler;

import javax.jms.ConnectionFactory;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.util.Collections.singletonList;

public class JmsListenerConfiguration {

    @Bean
    DefaultJmsListenerContainerFactory jmsListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory, PlatformTransactionManager transactionManager) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        factory.setErrorHandler(new LoggingErrorHandler());

        return factory;
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory(BeanFactory beanFactory) {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setBeanFactory(beanFactory);
        messageHandlerMethodFactory.setCustomArgumentResolvers(singletonList(new JsonPayloadHandlerMethodArgumentResolver()));

        return messageHandlerMethodFactory;
    }

    @Bean
    JmsListenerConfigurer jmsListenerConfigurer(MessageHandlerMethodFactory messageHandlerMethodFactory) {
        return (registrar) -> registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
    }


    private static class JsonPayloadHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

        private final ObjectMapper objectMapper;

        JsonPayloadHandlerMethodArgumentResolver() {
            this.objectMapper = fieldAccessEnabledObjectMapper();
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return true;
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
            String payload = payload(message);
            Class<?> payloadType = parameter.getParameterType();

            return this.objectMapper.readValue(payload, payloadType);
        }

        private static String payload(Message<?> message) {
            Object payload = message.getPayload();

            if (!(payload instanceof String)) {
                throw new IllegalStateException("unable to get non-string payload from message '" + message + "'");
            }

            return (String) payload;
        }

        private static ObjectMapper fieldAccessEnabledObjectMapper() {
            ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
                    .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
                    .build();

            objectMapper.setVisibility(FIELD, ANY);

            return objectMapper;
        }

    }


    private static class LoggingErrorHandler implements ErrorHandler {

        private static Logger LOGGER = LoggerFactory.getLogger(LoggingErrorHandler.class);

        @Override
        public void handleError(Throwable t) {
            LOGGER.error("error while processing incoming message", t);
        }

    }

}
