package inpeace.authenticationservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectPostProcessor<Object> objectPostProcessor;

    public SecurityConfig(UserDetailsService userDetailsService,BCryptPasswordEncoder bCryptPasswordEncoder, ObjectPostProcessor<Object> objectPostProcessor){
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.objectPostProcessor = objectPostProcessor;
    }

    @Bean
    //  Beans set specifics for web security configuration
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests((requests) -> requests
            .anyRequest().permitAll()); // All other requests must be authenticated
        return http.build();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);
        builder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return builder.build();
    }
}