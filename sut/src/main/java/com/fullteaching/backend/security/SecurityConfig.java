package com.fullteaching.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration. In this class can be configured several aspects
 * related to security:
 * Security behavior: Login method, session management, CSRF, etc.
 * Authentication provider: Responsible to authenticate users. In this
 * example, we use an instance of UserRepositoryAuthProvider, that authenticate
 * users stored in a Spring Data database.
 * URL Access Authorization: Access to http URLs depending on Authenticated
 * vs anonymous users and also based on user role.
 * <p>
 * <p>
 * NOTE: The only part of this class intended for app developer customization is
 * the method configureUrlAuthorization. App developer should
 * decide what URLs are accessible by what user role.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public final UserRepositoryAuthProvider userRepoAuthProvider;

    @Autowired
    public SecurityConfig (UserRepositoryAuthProvider userRepoAuthProv){
        this.userRepoAuthProvider = userRepoAuthProv;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(userRepoAuthProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)  {

        configureUrlAuthorization(http);

        // Use Http Basic Authentication
        http.httpBasic(Customizer.withDefaults());

        // Do not redirect when logout
        http.logout(logout -> logout.logoutSuccessHandler((rq, rs, a) -> {
        }));

        return http.build();
    }

    private void configureUrlAuthorization(HttpSecurity http)  {

        http.csrf(AbstractHttpConfigurer::disable);

        // APP: This rules have to be changed by app developer
        String studentRole = "STUDENT";
        String teacherRole = "TEACHER";
        String coursesPath = "/courses/**";
        String sessionsPath = "/sessions/**";
        String filesPath = "/files/**";

        // URLs that need authentication to access to it
        http.authorizeHttpRequests(auth -> auth
                // Courses API
                .requestMatchers(HttpMethod.GET, coursesPath).hasAnyRole(teacherRole, studentRole)
                .requestMatchers(HttpMethod.POST, coursesPath).hasRole(teacherRole)
                .requestMatchers(HttpMethod.PUT, coursesPath).hasRole(teacherRole)
                .requestMatchers(HttpMethod.DELETE, coursesPath).hasRole(teacherRole)
                // Forum API
                .requestMatchers(HttpMethod.POST, "/entries/**").hasAnyRole(teacherRole, studentRole)
                .requestMatchers(HttpMethod.POST, "/comments/**").hasAnyRole(teacherRole, studentRole)
                // Session API
                .requestMatchers(HttpMethod.POST, sessionsPath).hasRole(teacherRole)
                .requestMatchers(HttpMethod.PUT, sessionsPath).hasRole(teacherRole)
                .requestMatchers(HttpMethod.DELETE, sessionsPath).hasRole(teacherRole)
                // Files API
                .requestMatchers(HttpMethod.POST, filesPath).hasRole(teacherRole)
                .requestMatchers(HttpMethod.PUT, filesPath).hasRole(teacherRole)
                .requestMatchers(HttpMethod.DELETE, filesPath).hasRole(teacherRole)
                // Files upload/download API
                .requestMatchers(HttpMethod.POST, "/load-files/upload/course/**").hasRole(teacherRole)
                .requestMatchers(HttpMethod.POST, "/load-files/upload/picture/**").hasAnyRole(teacherRole, studentRole)
                .requestMatchers(HttpMethod.GET, "/load-files/course/**").hasAnyRole(teacherRole, studentRole)
                // Pictures
                .requestMatchers(HttpMethod.GET, "/assets/pictures/*").authenticated()
                // Other URLs can be accessed without authentication
                .anyRequest().permitAll()
        );
    }
}
