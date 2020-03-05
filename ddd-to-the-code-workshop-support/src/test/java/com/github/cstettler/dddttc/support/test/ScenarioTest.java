package com.github.cstettler.dddttc.support.test;


import com.github.cstettler.dddttc.support.EnableComponentScanExclusions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@ExtendWith(SpringExtension.class)
@ExtendWith(DatabaseCleaner.class)
@SpringBootTest
@WithDomainEventTestSupport
@AutoConfigureTestDatabase
@EnableComponentScanExclusions
@ActiveProfiles("test")
public @interface ScenarioTest {

}
