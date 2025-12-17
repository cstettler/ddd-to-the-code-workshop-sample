package com.github.cstettler.dddttc.registration.infrastructure.persistence;

import com.github.cstettler.dddttc.registration.domain.UserHandle;
import com.github.cstettler.dddttc.registration.domain.UserHandleAlreadyInUseException;
import com.github.cstettler.dddttc.registration.domain.UserRegistration;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationId;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationNotExistingException;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationRepository;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.cstettler.dddttc.registration.domain.FullName.fullName;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumber.phoneNumber;
import static com.github.cstettler.dddttc.registration.domain.UserHandle.userHandle;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationBuilder.userRegistration;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationId.userRegistrationId;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationMatcher.userRegistrationWith;
import static com.github.cstettler.dddttc.registration.domain.VerificationCode.verificationCode;
import static com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher.hasState;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@JdbcTest
@ContextConfiguration(classes = JdbcUserRegistrationRepositoryTests.LocalConfiguration.class)
class JdbcUserRegistrationRepositoryTests {

    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    void addAndGet_newUserRegistration_storesUserRegistrationInDatabase() {
        // arrange
        UserRegistration userRegistration = userRegistration()
                .id("user-registration-1")
                .userHandle("peter")
                .phoneNumber("+41 79 123 45 67")
                .phoneNumberVerificationCode("123456")
                .phoneNumberVerified(true)
                .fullName("Peter", "Meier")
                .completed(true)
                .build();

        // act
        this.userRegistrationRepository.add(userRegistration);
        UserRegistration loadedUserRegistration = this.userRegistrationRepository.get(userRegistration.id());

        // assert
        assertThat(rowCountInDatabase("user_registration"), is(1));

        assertThat(loadedUserRegistration, hasState(userRegistrationWith()
                .id(is(userRegistrationId("user-registration-1")))
                .userHandle(is(userHandle("peter")))
                .phoneNumber(is(phoneNumber("+41 79 123 45 67")))
                .phoneNumberVerificationCode(is(verificationCode("123456")))
                .phoneNumberVerified(is(true))
                .fullName(is(fullName("Peter", "Meier")))
                .completed(is(true))
        ));
    }

    @Test
    void add_userRegistrationWithSameUserHandleAlreadyExisting_throwsUserHandleAlreadyInUseException() {
        // arrange
        UserRegistration existingUserRegistration = userRegistration()
                .id("user-registration-1")
                .userHandle("peter")
                .build();
        this.userRegistrationRepository.add(existingUserRegistration);

        UserRegistration userRegistration = userRegistration()
                .id("user-registration-2")
                .userHandle("peter")
                .build();

        // act + assert
        assertThrows(UserHandleAlreadyInUseException.class, () -> this.userRegistrationRepository.add(userRegistration));
    }

    @Test
    void update_existingUserRegistration_updatesExistingUserRegistration() {
        // arrange
        UserRegistration existingUserRegistration = userRegistration()
                .id("user-registration-1")
                .userHandle("peter")
                .phoneNumber("+41 79 123 45 67")
                .phoneNumberVerificationCode("123456")
                .phoneNumberVerified(true)
                .fullName("Peter", "Meier")
                .completed(true)
                .build();

        this.userRegistrationRepository.add(existingUserRegistration);

        UserRegistration updatedUserRegistration = userRegistration()
                .id("user-registration-1")
                .userHandle("petra")
                .phoneNumber("+41 79 987 65 43")
                .phoneNumberVerificationCode("987654")
                .phoneNumberVerified(false)
                .fullName("Petra", "Maier")
                .completed(false)
                .build();

        // act
        this.userRegistrationRepository.update(updatedUserRegistration);
        UserRegistration loadedUserRegistration = this.userRegistrationRepository.get(existingUserRegistration.id());

        // assert
        assertThat(rowCountInDatabase("user_registration"), is(1));

        assertThat(loadedUserRegistration, hasState(userRegistrationWith()
                .id(is(userRegistrationId("user-registration-1")))
                .userHandle(is(userHandle("petra")))
                .phoneNumber(is(phoneNumber("+41 79 987 65 43")))
                .phoneNumberVerificationCode(is(verificationCode("987654")))
                .phoneNumberVerified(is(false))
                .fullName(is(fullName("Petra", "Maier")))
                .completed(is(false))
        ));
    }

    @Test
    void update_nonExistingUserRegistration_throwsUserRegistrationNotExistingException() {
        // arrange
        UserRegistration userRegistration = userRegistration()
                .id("user-registration-1")
                .userHandle("peter")
                .phoneNumber("+41 79 123 45 67")
                .phoneNumberVerificationCode("123456")
                .phoneNumberVerified(true)
                .fullName("Peter", "Meier")
                .completed(true)
                .build();

        // act + assert
        assertThrows(UserRegistrationNotExistingException.class, () -> this.userRegistrationRepository.update(userRegistration));
    }

    @Test
    void get_nonExistingUserRegistration_throwsUserRegistrationNotExistingException() {
        // arrange
        UserRegistrationId userRegistrationId = userRegistrationId("non-existing-user-registration");

        // act + assert
        assertThrows(UserRegistrationNotExistingException.class, () -> this.userRegistrationRepository.get(userRegistrationId));
    }

    @Test
    void find_existingUserRegistration_returnsExistingUserRegistration() {
        // arrange
        UserRegistration existingUserRegistration = userRegistration()
                .id("user-registration-1")
                .userHandle("peter")
                .phoneNumber("+41 79 123 45 67")
                .phoneNumberVerificationCode("123456")
                .phoneNumberVerified(true)
                .fullName("Peter", "Meier")
                .completed(true)
                .build();

        this.userRegistrationRepository.add(existingUserRegistration);

        // act
        UserRegistration loadedUserRegistration = this.userRegistrationRepository.find(userHandle("peter"));

        // assert
        assertThat(loadedUserRegistration, hasState(userRegistrationWith()
                .id(is(userRegistrationId("user-registration-1")))
                .userHandle(is(userHandle("peter")))
                .phoneNumber(is(phoneNumber("+41 79 123 45 67")))
                .phoneNumberVerificationCode(is(verificationCode("123456")))
                .phoneNumberVerified(is(true))
                .fullName(is(fullName("Peter", "Meier")))
                .completed(is(true))
        ));
    }

    @Test
    void find_nonExistingUserRegistration_returnsNull() {
        // arrange
        UserHandle userHandle = userHandle("non-existing-user-handle");

        // act
        UserRegistration loadedUserRegistration = this.userRegistrationRepository.find(userHandle);

        // assert
        assertThat(loadedUserRegistration, is(nullValue()));
    }

    @SuppressWarnings("ConstantConditions")
    private int rowCountInDatabase(String tableName) {
        return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, EmptySqlParameterSource.INSTANCE, Integer.class);
    }


    static class LocalConfiguration {

        @Bean
        JdbcUserRegistrationRepository userRegistrationRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new JdbcUserRegistrationRepository(jdbcTemplate, mock(DomainEventPublisher.class));
        }

    }

}