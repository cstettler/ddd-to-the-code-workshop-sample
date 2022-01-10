package com.github.cstettler.dddttc.rental;

import com.github.cstettler.dddttc.rental.domain.booking.BookingCompletedEvent;
import com.github.cstettler.dddttc.stereotype.AggregateFactory;
import com.github.cstettler.dddttc.stereotype.ApplicationService;
import com.github.cstettler.dddttc.stereotype.DomainService;
import com.github.cstettler.dddttc.support.InfrastructureServiceImplementationFilter;
import com.github.cstettler.dddttc.support.RepositoryImplementationFilter;
import com.github.cstettler.dddttc.support.infrastructure.event.DomainEventSupportConfiguration;
import com.github.cstettler.dddttc.support.infrastructure.event.DomainEventTypeMappings;
import com.github.cstettler.dddttc.support.infrastructure.event.JmsListenerConfiguration;
import com.github.cstettler.dddttc.support.infrastructure.persistence.TransactionConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.time.Clock;

import static com.github.cstettler.dddttc.support.infrastructure.event.DomainEventTypeMappings.domainEventTypeMappings;
import static org.springframework.context.annotation.FilterType.ANNOTATION;
import static org.springframework.context.annotation.FilterType.CUSTOM;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.github.cstettler.dddttc.rental",
                "com.github.cstettler.dddttc.support"
        },
        includeFilters = {
                @ComponentScan.Filter(type = ANNOTATION, classes = ApplicationService.class),
                @ComponentScan.Filter(type = ANNOTATION, classes = DomainService.class),
                @ComponentScan.Filter(type = ANNOTATION, classes = AggregateFactory.class),
                @ComponentScan.Filter(type = CUSTOM, classes = RepositoryImplementationFilter.class),
                @ComponentScan.Filter(type = CUSTOM, classes = InfrastructureServiceImplementationFilter.class)
        },
        excludeFilters = {
                @ComponentScan.Filter(type = CUSTOM, classes = TypeExcludeFilter.class),
                @ComponentScan.Filter(type = CUSTOM, classes = AutoConfigurationExcludeFilter.class)
        }
)
@Import({
        DomainEventSupportConfiguration.class,
        TransactionConfiguration.class,
        JmsListenerConfiguration.class
})
public class RentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalApplication.class, args);
    }

    @Bean
    DomainEventTypeMappings registrationDomainEventTypeMappings() {
        return domainEventTypeMappings()
                .addMapping(BookingCompletedEvent.class, "rental/booking-completed")
                .build();
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

}
