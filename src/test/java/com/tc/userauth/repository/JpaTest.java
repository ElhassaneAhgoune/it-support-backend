package com.tc.userauth.repository;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.tc.userauth.config.JpaConfig;
import com.tc.userauth.config.PostgresContainerConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("tests")
@AutoConfigureTestDatabase(replace = NONE)
@Import({JpaConfig.class, PostgresContainerConfig.class})
public abstract class JpaTest {

}