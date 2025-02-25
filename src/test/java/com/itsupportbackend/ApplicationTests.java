package com.itsupportbackend;


import com.itsupportbackend.config.OracleContainerConfig;
import com.itsupportbackend.config.RedisContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("tests")
@Import({OracleContainerConfig.class, RedisContainerConfig.class})
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
