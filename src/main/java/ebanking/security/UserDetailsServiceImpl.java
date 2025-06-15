package ebanking.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ebanking.model.UserEntity;
import ebanking.service.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

//    @Value("${app.test.username:P-0123456789}")
//    @Value("${app.test.username:alice}")
//    private String testUsername;
//
//    @Value("${app.test.password:alice123}")
//    private String testPasswordPlain;
	
	@Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;   // BCMathEncoder

    //The user is already authenticated and the API client invoking the transaction API will send a JWT token containing the user’s unique identity key (e.g. P-0123456789)
	//如上需求使用者已經預先完成通過登入驗證，故這邊不特別另外設計資料庫及使用者驗證的步驟
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        
    	UserEntity user = userService.loadByUsername(username);
        
        return new CustomUserDetails(
            user.getUsername(),
            user.getPasswordHash(),
            user.getCustomerId(),
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        //===搭配private String testUsername;的寫法===
//        if (!testUsername.equals(username)) {
//            throw new UsernameNotFoundException("User not found: " + username);
//        }
//        String pwHash     = userRepo.findPasswordByUsername(username);
//        String hashed = passwordEncoder.encode(testPasswordPlain);
        
        //這邊應該要再查詢資料庫抓取使用者權限等級供後續權限設定，直接設定為ROLE_USER
//      return new User(testUsername, hashed, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        //===搭配private String testUsername;的寫法===
      

    }
}
