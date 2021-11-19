package io.github.cyronlee.config;

import io.github.cyronlee.repository.SoftDeleteRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "io.github.cyronlee.repository",
        repositoryBaseClass = SoftDeleteRepositoryImpl.class)
public class JpaConfig {
}
