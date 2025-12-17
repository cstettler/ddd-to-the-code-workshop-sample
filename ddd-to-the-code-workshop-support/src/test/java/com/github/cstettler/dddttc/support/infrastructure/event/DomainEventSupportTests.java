package com.github.cstettler.dddttc.support.infrastructure.event;

import com.github.cstettler.dddttc.support.EnableComponentScanExclusions;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static com.github.cstettler.dddttc.support.infrastructure.event.TestDomainEvent.testDomainEvent;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@EnableComponentScanExclusions
@ActiveProfiles("test")
class DomainEventSupportTests {

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    @Autowired
    private TestDomainService testDomainService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void clearRecordedDomainEvents() {
        this.testDomainService.clearRecordedDomainEvents();
    }

    @Test
    void publish_testDomainEventAndDomainEventHandler_dispatchesDomainEventToDomainEventHandler() {
        // arrange
        TestDomainEvent testDomainEvent = testDomainEvent("test-value");

        // act
        executeInTransaction(() -> this.domainEventPublisher.publish(testDomainEvent));

        // assert
        await().atMost(3, SECONDS).until(() -> this.testDomainService.recordedDomainEvents().size() > 0);

        assertThat(this.testDomainService.recordedDomainEvents(), hasSize(1));
        assertThat(this.testDomainService.recordedDomainEvents().get(0).value(), is("test-value"));
    }

    private void executeInTransaction(Executable executable) {
        new TransactionTemplate(this.transactionManager).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    executable.execute();
                } catch (Throwable t) {
                    throw new IllegalStateException("execution in transaction failed", t);
                }
            }
        });
    }

}