package com.github.cstettler.dddttc.registration;

import com.github.cstettler.dddttc.registration.domain.PhoneNumberVerificationCodeGeneratedEvent;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationCompletedEvent;
import com.github.cstettler.dddttc.stereotype.AggregateFactory;
import com.github.cstettler.dddttc.stereotype.ApplicationService;
import com.github.cstettler.dddttc.stereotype.DomainService;
import com.github.cstettler.dddttc.support.InfrastructureServiceImplementationFilter;
import com.github.cstettler.dddttc.support.RepositoryImplementationFilter;
import com.github.cstettler.dddttc.support.infrastructure.event.DomainEventSupportConfiguration;
import com.github.cstettler.dddttc.support.infrastructure.event.DomainEventTypeMappings;
import com.github.cstettler.dddttc.support.infrastructure.persistence.TransactionConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import static com.github.cstettler.dddttc.support.infrastructure.event.DomainEventTypeMappings.domainEventTypeMappings;
import static org.springframework.context.annotation.FilterType.ANNOTATION;
import static org.springframework.context.annotation.FilterType.CUSTOM;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.github.cstettler.dddttc.registration",
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
})
public class RegistrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistrationApplication.class, args);
    }

    @Bean
    DomainEventTypeMappings registrationDomainEventTypeMappings() {
        return domainEventTypeMappings()
                .addMapping(PhoneNumberVerificationCodeGeneratedEvent.class, "registration/phone-number-verification-code-generated")
                .addMapping(UserRegistrationCompletedEvent.class, "registration/user-registration-completed")
                .build();
    }

}
