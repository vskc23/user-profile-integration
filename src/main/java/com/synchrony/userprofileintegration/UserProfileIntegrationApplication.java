package com.synchrony.userprofileintegration;

import com.synchrony.userprofileintegration.config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserProfileIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(UserProfileIntegrationApplication.class);
        application.addInitializers(new DotenvInitializer());
        application.run(args);
    }
}