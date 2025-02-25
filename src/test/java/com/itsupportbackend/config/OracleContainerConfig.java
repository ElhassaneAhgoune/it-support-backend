package com.itsupportbackend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class OracleContainerConfig {

    @Bean
    OracleContainer oracleContainer() {
        OracleContainer container = new OracleContainer(DockerImageName.parse("gvenzl/oracle-xe:latest"))
                .withEnv("ORACLE_PASSWORD", "verYs3cret")
                .withExposedPorts(1521);
        container.start(); // Explicitly start the container
        return container;
    }
}
