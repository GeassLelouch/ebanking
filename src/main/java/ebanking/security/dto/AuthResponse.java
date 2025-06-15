// src/main/java/ebanking/security/dto/AuthResponse.java
package ebanking.security.dto;

public class AuthResponse {
    private final String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
