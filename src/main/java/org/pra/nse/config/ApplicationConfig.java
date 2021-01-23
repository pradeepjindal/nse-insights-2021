package org.pra.nse.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

//@Configuration
public class ApplicationConfig {
    @Bean
    public Object yamlPropertySourceLoader() throws IOException {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        Object applicationYamlPropertySource = loader.load("upload-queries.yaml", new ClassPathResource("upload-queries.yaml"));
        return applicationYamlPropertySource;
    }

}
