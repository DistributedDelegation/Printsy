package authentication.security;

import authentication.repository.UserRepository;
import authentication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("Email address not found: " + emailAddress));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        return new CustomUser(user.getUserID(), user.getEmailAddress(), user.getEncodedPassword(), authorities);
    }
}
