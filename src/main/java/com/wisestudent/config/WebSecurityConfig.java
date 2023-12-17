package com.wisestudent.config;

import com.wisestudent.models.Role;
import com.wisestudent.security.TokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final TokenFilter tokenFilter;

    private final String AUTH_URL = "/wise-students/auth/**";
    private final String[] USER_URL = new String[]{
            "/wise-students/posts",
            "/wise-students/post",
            "/wise-students/post/comments"
    };

    private final String[] ONLY_GET_URL = new String[]{
            "/wise-students/subjects",
            "/wise-students/post-types"
    };

    private final String[] ADMIN_URL = new String[]{
            "/wise-students/admin/**"
    };

    private final String[] I_LOVE_SPRING_VERY_MUCH = new String[]{
            "/wise-students/news/",
            "/wise-students/post/comments/moder/"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
//        http.cors().disable();
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(AUTH_URL).permitAll()
                .requestMatchers(HttpMethod.GET, ONLY_GET_URL).authenticated()
                .requestMatchers(HttpMethod.GET, "/wise-students/news/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/wise-students/news/thread/**").authenticated()  
                .requestMatchers(USER_URL).authenticated()
                .requestMatchers("/wise-students/post/comments/moder/**").hasAuthority(String.valueOf(Role.ROLE_MODERATOR))
                .requestMatchers("/wise-students/post/comments/moder/**").hasAuthority(String.valueOf(Role.ROLE_ADMIN))
                .requestMatchers("/wise-students/news/**").hasAuthority(String.valueOf(Role.ROLE_MODERATOR))
                .requestMatchers("/wise-students/news/**").hasAuthority(String.valueOf(Role.ROLE_ADMIN))
                .requestMatchers(ADMIN_URL).hasAuthority(String.valueOf(Role.ROLE_ADMIN))
                .requestMatchers(ONLY_GET_URL).hasAuthority(String.valueOf(Role.ROLE_ADMIN))
                .anyRequest().permitAll()
        );

        return http.build();
    }

}
