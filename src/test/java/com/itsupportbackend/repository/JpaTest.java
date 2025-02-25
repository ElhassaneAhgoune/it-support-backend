package com.itsupportbackend.repository;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.itsupportbackend.config.JpaConfig;
import com.itsupportbackend.config.OracleContainerConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("tests")
@AutoConfigureTestDatabase(replace = NONE)
@Import({JpaConfig.class, OracleContainerConfig.class})
public abstract class JpaTest {

}