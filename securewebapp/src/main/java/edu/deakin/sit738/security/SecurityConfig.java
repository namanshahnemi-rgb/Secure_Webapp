package edu.deakin.sit738.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
              .antMatchers("/vuln/**","/fix/**","/","/h2-console/**","/css/**","/js/**","/static/**").permitAll()
              .anyRequest().authenticated()
            .and()
              .formLogin().defaultSuccessUrl("/", true)
            .and()
              .logout().permitAll();

        // For H2 console in dev:
        http.csrf().ignoringAntMatchers("/vuln/**");
        http.headers().frameOptions().sameOrigin();

        return http.build();
    }
}
