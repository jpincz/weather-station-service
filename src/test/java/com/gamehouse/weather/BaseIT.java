package com.gamehouse.weather;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PostgresContextInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT {
}