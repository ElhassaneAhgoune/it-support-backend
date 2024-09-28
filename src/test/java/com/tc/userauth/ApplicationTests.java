package com.tc.userauth;

import com.tc.userauth.config.PostgresContainerConfig;
import com.tc.userauth.config.RedisContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("tests")
@Import({PostgresContainerConfig.class, RedisContainerConfig.class})
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
