package com.github.cstettler.dddttc.rental.infrastructure.bootstrap;

import com.github.cstettler.dddttc.rental.domain.bike.InitializeBikesService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;

@Component
class BikeInitializationTrigger {

    private final InitializeBikesService initializeBikesService;
    private final TransactionTemplate transactionTemplate;

    BikeInitializationTrigger(InitializeBikesService initializeBikesService, PlatformTransactionManager transactionManager) {
        this.initializeBikesService = initializeBikesService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @PostConstruct
    void triggerBikeInitialization() {
        this.transactionTemplate.execute((status) -> {
            this.initializeBikesService.initializeBikes();

            return null;
        });
    }

}
