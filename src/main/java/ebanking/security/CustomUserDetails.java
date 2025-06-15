package ebanking.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final String customerId;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(String username,
                             String password,
                             String customerId,
                             List<GrantedAuthority> authorities) {
        this.username    = username;
        this.password    = password;
        this.customerId  = customerId;
        this.authorities = authorities;
    }

    public String getCustomerId() {
        return customerId;
    }

    // ===== 以下是 UserDetails 介面方法必須實作 =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /** 帳號是否過期？通常跟業務邏輯綁定，若無需求可都回 true */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** 帳號是否鎖定中？若無鎖帳機制可都回 true */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** 密碼是否過期？若無密碼過期機制可都回 true */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** 是否啟用？若有停權機制，依實際狀態回傳 */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
