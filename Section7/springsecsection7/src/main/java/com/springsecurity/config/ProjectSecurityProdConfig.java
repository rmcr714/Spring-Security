package com.springsecurity.config;


import static org.springframework.security.config.Customizer.withDefaults;

import com.springsecurity.exceptionhandling.CustomAccessDeniedHandler;
import com.springsecurity.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@Configuration
@Profile("prod")
public class ProjectSecurityProdConfig {

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    http.requiresChannel(rcc->rcc.anyRequest().requiresSecure())  //Allows only https, good for prod envs
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests((requests) -> requests
        .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
        .requestMatchers("/notices", "/contact", "/error","/register").permitAll());
    http.formLogin(withDefaults());
    //Since we are throwing custom exception, we need to have this lambda below
    http.httpBasic(hbc->hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));

    //Validation exception should be done at global level
    http.exceptionHandling(eh->eh.accessDeniedHandler(new CustomAccessDeniedHandler()));
    return http.build();
  }



  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /**
   * From Spring Security 6.3 version
   * @return
   */
  @Bean
  public CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }


}
