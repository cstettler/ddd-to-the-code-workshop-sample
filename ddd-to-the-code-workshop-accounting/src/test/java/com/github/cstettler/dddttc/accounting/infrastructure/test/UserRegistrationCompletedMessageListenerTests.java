package com.github.cstettler.dddttc.accounting.infrastructure.test;

import com.github.cstettler.dddttc.accounting.application.WalletService;
import com.github.cstettler.dddttc.support.test.ScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

import static com.github.cstettler.dddttc.accounting.domain.UserId.userId;
import static com.github.cstettler.dddttc.accounting.domain.WalletAlreadyExistsException.walletAlreadyExists;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


@ScenarioTest
class UserRegistrationCompletedMessageListenerTests {

    @MockBean
    private WalletService walletService;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    void initializeWalletOnUserRegistrationCompletedEventDelivery() {
        // arrange
        String domainEventType = "registration/user-registration-completed";
        String domainEventPayload = asJson("" +
                "{" +
                "  'userHandle': {" +
                "    'value': 'peter'" +
                "  }" +
                "}"
        );

        // act
        dispatchMessage(domainEventType, domainEventPayload);

        // assert
        await().atMost(3, SECONDS).untilAsserted(() -> verify(this.walletService).initializeWallet(userId("peter")));
    }

    @Test
    void initializeWalletOnUserRegistrationCompletedEventRedelivery() {
        // arrange
        String domainEventType = "registration/user-registration-completed";
        String domainEventPayload = asJson("" +
                "{" +
                "  'userHandle': {" +
                "    'value': 'peter'" +
                "  }" +
                "}"
        );

        doThrow(walletAlreadyExists(userId("peter"))).when(this.walletService).initializeWallet(userId("peter"));

        // act
        dispatchMessage(domainEventType, domainEventPayload);

        // assert
        await().atMost(3, SECONDS).untilAsserted(() -> verify(this.walletService).initializeWallet(userId("peter")));
    }

    private void dispatchMessage(String destination, String domainEventPayload) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(this.connectionFactory);
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.send(destination, (session) -> session.createTextMessage(domainEventPayload));
    }

    private static String asJson(String value) {
        return value.replace('\'', '"');
    }

}
