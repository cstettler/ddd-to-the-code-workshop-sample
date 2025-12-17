package com.github.cstettler.dddttc.accounting;

import com.github.cstettler.dddttc.stereotype.AggregateFactory;
import com.github.cstettler.dddttc.stereotype.ApplicationService;
import com.github.cstettler.dddttc.stereotype.DomainService;
import com.github.cstettler.dddttc.support.InfrastructureServiceImplementationFilter;
import com.github.cstettler.dddttc.support.RepositoryImplementationFilter;
import com.github.cstettler.dddttc.support.infrastructure.event.DomainEventSupportConfiguration;
import com.github.cstettler.dddttc.support.infrastructure.event.JmsListenerConfiguration;
import com.github.cstettler.dddttc.support.infrastructure.persistence.TransactionConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import static org.springframework.context.annotation.FilterType.ANNOTATION;
import static org.springframework.context.annotation.FilterType.CUSTOM;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.github.cstettler.dddttc.accounting",
                "com.github.cstettler.dddttc.support"
        },
        includeFilters = {
                @Filter(type = ANNOTATION, classes = ApplicationService.class),
                @Filter(type = ANNOTATION, classes = DomainService.class),
                @Filter(type = ANNOTATION, classes = AggregateFactory.class),
                @Filter(type = CUSTOM, classes = RepositoryImplementationFilter.class),
                @Filter(type = CUSTOM, classes = InfrastructureServiceImplementationFilter.class)
        },
        excludeFilters = {
                @Filter(type = CUSTOM, classes = TypeExcludeFilter.class),
                @Filter(type = CUSTOM, classes = AutoConfigurationExcludeFilter.class)
        }
)
@Import({
        DomainEventSupportConfiguration.class,
        TransactionConfiguration.class,
        JmsListenerConfiguration.class
})
public class AccountingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountingApplication.class, args);
    }

}