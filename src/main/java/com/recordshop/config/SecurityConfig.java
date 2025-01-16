package com.recordshop.config;

import com.recordshop.service.PrincipalOauth2UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    public SecurityConfig(@Lazy PrincipalOauth2UserService principalOauth2UserService) {
        this.principalOauth2UserService = principalOauth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // permitAll() 인증 없이 해당 경로 접근가능 / hasRole() 권한이 있는 자만 접근가능 하도록 URL설정
        http
                .authorizeHttpRequests(config->config
                        .requestMatchers("/css/**","/js/**","/image/**").permitAll()
                        .requestMatchers("/","/members/**","/item/**","/images/**" ,"main").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/members/myPage","/inquiries/**").authenticated()
                        .anyRequest().authenticated()
                );

        http
                .formLogin(config->
                        config.loginPage("/members/login")
                                .defaultSuccessUrl("/members/myPage", true)
                                .loginProcessingUrl("/login")
                                .usernameParameter("username")     //로그인화면에서 name=username 이면 생략가능 --> name=email
                                .failureUrl("/members/login/error")
                )
                .logout(logout->
                        logout.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                                .logoutSuccessUrl("/")
                );
        http
                .oauth2Login(form->{
                    form
                            .loginPage("/members/login")
                                .userInfoEndpoint(userInfoEndpointConfig -> {
                                    userInfoEndpointConfig.userService(principalOauth2UserService);
                                        });
        });



        //3.0이상 부터는 람다식으로 작성해야함.
        http
                .csrf(config->config.disable());

        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
