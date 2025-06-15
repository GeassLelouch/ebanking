package ebanking.security;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ebanking.security.dto.AuthRequest;
import ebanking.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserService    userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(
            UserService userService,
            AuthenticationManager authManager,
            JwtUtil jwtUtil
        ) {
            this.userService = userService;
            this.authManager = authManager;
            this.jwtUtil     = jwtUtil;
        }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest req) {
        // 注意：這裡一定要用 org.springframework.web.bind.annotation.RequestBody
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "帳號或密碼錯誤");
        }

        String customerId = userService.findCustomerIdByUsername(req.getUsername());
        String token = jwtUtil.generateToken(
                Map.of("username", req.getUsername()),  // 可帶 extraClaims
                customerId                               // 作為 subject
            );
//        String token = jwtUtil.generateToken(
//        	    Map.of(
//        	    	      "username", user.getUsername(),   // 如果你想同時在 claim 裡帶 username
//        	    	      "roles",     user.getRoles()       // 或其他 custom claims
//        	    	    ),
//        	    	    customerId
//        	    	);
        return Map.of("token", token);
    }
}
