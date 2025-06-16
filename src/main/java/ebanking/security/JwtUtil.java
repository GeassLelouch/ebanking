package ebanking.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey  publicKey;
    private final long          jwtExpirationInMs;

//    public JwtUtil(
//        @Value("${spring.security.jwt.private-key}")    Resource privateKeyPem,
//        @Value("${spring.security.jwt.public-key}")     Resource publicKeyPem,
//        @Value("${spring.security.jwt.expiration-in-ms}") long jwtExpirationInMs
//    ) throws Exception {
//        this.privateKey       = loadPrivateKey(privateKeyPem);
//        this.publicKey        = loadPublicKey(publicKeyPem);
//        this.jwtExpirationInMs = jwtExpirationInMs;
//    }
    
    public JwtUtil(
        @Value("${spring.security.jwt.private-key}")    String privateKeyPem,
        @Value("${spring.security.jwt.public-key}")     String publicKeyPem,
        @Value("${spring.security.jwt.expiration-in-ms}") long jwtExpirationInMs
    ) throws Exception {
        this.privateKey       = parsePrivateKey(privateKeyPem);
        this.publicKey        = parsePublicKey(publicKeyPem);
        this.jwtExpirationInMs = jwtExpirationInMs;
    }
    
    private RSAPrivateKey parsePrivateKey(String pem) throws Exception {
        // 1. 去除 header/footer
        String clean = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            // 2. 去除所有空白（包含換行、空格、tab）
            .replaceAll("\\s+", "");

        // 3. Base64 解碼
        byte[] keyBytes = Base64.getDecoder().decode(clean);

        // 4. PKCS8 規格建 KeySpec
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }

    private RSAPublicKey parsePublicKey(String pem) throws Exception {
        String clean = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(clean);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
    }

    
//    private RSAPrivateKey loadPrivateKey(Resource res) throws Exception {
//        String pem = new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//        String base64 = pem
//            // 精确移除 BEGIN/END 行
//            .replaceAll("-----BEGIN PRIVATE KEY-----", "")
//            .replaceAll("-----END PRIVATE KEY-----", "")
//            // 移除所有空白字元（換行、空格、tab）
//            .replaceAll("\\s", "");
//        byte[] keyBytes = Base64.getDecoder().decode(base64);
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
//    }
//
//    private RSAPublicKey loadPublicKey(Resource res) throws Exception {
//        String pem = new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//        String base64 = pem
//            .replaceAll("-----BEGIN PUBLIC KEY-----", "")
//            .replaceAll("-----END PUBLIC KEY-----", "")
//            .replaceAll("\\s", "");
//        byte[] keyBytes = Base64.getDecoder().decode(base64);
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
//    }


//    // ------------ Key Parsing ------------
//    private RSAPrivateKey parsePrivateKey(String pem) throws Exception {
//        String base64 = pem;
//        byte[] bytes = Base64.getDecoder().decode(base64);
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
//        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
//    }
//
//    private RSAPublicKey parsePublicKey(String pem) throws Exception {
//        String base64 = pem;
//        byte[] bytes = Base64.getDecoder().decode(base64);
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
//        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
//    }

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
