package ebanking.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Value("${app.test.username:user123}")
    private String testUsername;

    @Value("${app.test.password:$2a$10$zcCK.R0SM9C3jPp3/TdSPOX.7Epap8i1IR2/M5bkZhndEy1rak9bm}")
    private String testPasswordHash;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!testUsername.equals(username)) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new User(testUsername, testPasswordHash,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
