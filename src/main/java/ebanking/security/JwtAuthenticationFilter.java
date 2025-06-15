package ebanking.security;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//註解掉的原因SecurityConfig 用 @Bean 已經手動註冊這個 Filter
//@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    private final JwtUtil jwtUtil;

    private final UserDetailsService userDetailsService;;
    
    public JwtAuthenticationFilter(JwtUtil jwtUtils,
	            UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtils;
		this.userDetailsService = userDetailsService;
	}

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        String token = null;
        String username = null;
        String customerId = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
            	username = jwtUtil.extractUsernameClaim(token);
            	customerId = jwtUtil.extractUsername(token);
//            	System.out.println("Username in claim: " + username);
//                username = jwtUtil.extractUsername(token);
            } catch (Exception ex) {
                // 1. 準備 Taipei 時區與格式器
                ZoneId taipeiZone = ZoneId.of("Asia/Taipei");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

                // 2. 嘗試從 ExpiredJwtException 拿 exp date
                ZonedDateTime expTaipei;
                if (ex instanceof ExpiredJwtException eje) {
                    Date expDate    = eje.getClaims().getExpiration();
                    expTaipei       = expDate.toInstant().atZone(taipeiZone);
                } else {
                    // 如果不是過期例外，就把 expTaipei 設成 now
                    expTaipei = Instant.now().atZone(taipeiZone);
                }

                // 3. 拿現在時間的 Taipei 版本
                ZonedDateTime nowTaipei = Instant.now().atZone(taipeiZone);

                // 4. 格式化並印到 log
                String expStr = expTaipei.format(fmt);
                String nowStr = nowTaipei.format(fmt);
                
                logger.error("Cannot extract username from JWT: {} (Taipei), current time: {} (Taipei)",
                        expStr, nowStr);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
