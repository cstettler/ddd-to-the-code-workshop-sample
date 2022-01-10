package com.github.cstettler.dddttc.rental;

import com.github.cstettler.dddttc.support.EnableComponentScanExclusions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableComponentScanExclusions
@ActiveProfiles("test")
class RentalApplicationTests {

    @Test
    void bootstrappingApplicationContext_works() {
        // do nothing
    }

}