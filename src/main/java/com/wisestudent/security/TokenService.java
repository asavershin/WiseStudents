package com.wisestudent.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Date;

@Service
public class TokenService {

    @Value("${authToken.key}")
    private String tokenKey;

    @Value("${authToken.expiration}")
    private Long expiration;

    public String generateToken(String username) {
        Date date = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(date)
                .expiration(
                        new Date(
                                date.getTime() + expiration
                        )
                )
                .signWith(SignatureAlgorithm.HS256, tokenKey)
                .compact();
    }

    public String parseToken(HttpServletRequest request) {
        try {
            return WebUtils.getCookie(request, "authToken").getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return
                Jwts.parser()
                        .setSigningKey(tokenKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
    }


}
