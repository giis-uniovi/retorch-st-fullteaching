package com.fullteaching.backend;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.UserComponent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
//ONLY ON PRODUCTION

@SpringBootApplication
@EnableWebSocket
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public AuthorizationService authorizationService(ObjectProvider<UserComponent> userProvider) {
        return new AuthorizationService(userProvider);
    }

    //ONLY ON PRODUCTION
    @Bean
    public AmazonS3 s3client() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withRegion(Regions.DEFAULT_REGION)
                .build();
    }
    //ONLY ON PRODUCTION

}
