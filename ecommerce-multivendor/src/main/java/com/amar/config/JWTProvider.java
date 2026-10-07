package com.amar.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class JWTProvider {

    SecretKey key =
            Keys.hmacShaKeyFor(
                    JWt_CONSTANT.SECRET_KEY.getBytes()
            );


    public String generateJWT(
            Authentication auth) {

        Collection<? extends GrantedAuthority>
                authorities =
                auth.getAuthorities();

        String roles =
                populateAuthorities(authorities);

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                new Date().getTime()
                                        + 86400000
                        )
                )
                .claim(
                        "email",
                        auth.getName()
                )
                .claim(
                        "authorities",
                        roles
                )
                .signWith(key)
                .compact();
    }


    public String getEmailFromJWTToken(
            String jwt) {

        if (jwt == null ||
                !jwt.startsWith("Bearer ")) {

            throw new IllegalArgumentException(
                    "Invalid Authorization header"
            );
        }

        jwt = jwt.substring(7);

        Claims claims =
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

        return String.valueOf(
                claims.get("email")
        );
    }


    public String getEmailFromJwtToken(
            String jwt) {

        return getEmailFromJWTToken(jwt);
    }


    private String populateAuthorities(
            Collection<? extends GrantedAuthority>
                    authorities) {

        Set<String> auth =
                new HashSet<>();

        for (GrantedAuthority authority :
                authorities) {

            auth.add(
                    authority.getAuthority()
            );
        }

        return String.join(",", auth);
    }
}