package ebanking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey  publicKey;
    private final long          jwtExpirationInMs;

    public JwtUtil(
        @Value("${spring.security.jwt.private-key}")    String privateKeyPem,
        @Value("${spring.security.jwt.public-key}")     String publicKeyPem,
        @Value("${spring.security.jwt.expiration-in-ms}") long jwtExpirationInMs
    ) throws Exception {
        this.privateKey       = parsePrivateKey(privateKeyPem);
        this.publicKey        = parsePublicKey(publicKeyPem);
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    // ------------ Key Parsing ------------
    private RSAPrivateKey parsePrivateKey(String pem) throws Exception {
        String base64 = pem;
        byte[] bytes = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private RSAPublicKey parsePublicKey(String pem) throws Exception {
        String base64 = pem;
        byte[] bytes = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    // ------------ Claim Extraction ------------
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
 // 引入 io.jsonwebtoken.Claims, Function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts
            .parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claimsResolver.apply(claims);
    }
    
    public String extractUsernameClaim(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        Claims claims = parseClaims(token);
//        return claimsResolver.apply(claims);
//    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // ------------ Token Generation ------------
    public String generateToken(String username) {
        return generateToken(Map.of(), username);
    }

    public String generateToken(Map<String, Object> extraClaims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(subject)
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + jwtExpirationInMs))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
    }

    // ------------ Validation ------------
    public boolean validateToken(String token, String userDetailsUsername) {
        final String tokenUsername  = extractUsernameClaim(token);
        return (tokenUsername .equals(userDetailsUsername) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
