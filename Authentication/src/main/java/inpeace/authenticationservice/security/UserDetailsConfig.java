package inpeace.authenticationservice.security;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import inpeace.authenticationservice.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class UserDetailsConfig implements UserDetailsService{

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsConfig(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public CustomUser loadUserByUsername(String emailAddress) throws UsernameNotFoundException {
        inpeace.authenticationservice.model.User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("Email address not found: " + emailAddress));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        return new CustomUser(user.getUserID(), user.getEmailAddress(), user.getEncodedPassword(), authorities);
    }
}
