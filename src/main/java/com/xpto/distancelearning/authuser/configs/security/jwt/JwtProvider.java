package com.xpto.distancelearning.authuser.configs.security.jwt;

import com.xpto.distancelearning.authuser.configs.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Log4j2
@Component
public class JwtProvider {

    @Value("${dl.auth.jwtSecret}")
    private String jwtSecret;

    @Value("${dl.auth.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwt(Authentication authentication) {
        UserDetails userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                .signWith(SignatureAlgorithm.HS512, jwtSecret) // working deprecated method. If I used this line then getUsernameJwt() and validateJwt() methods must 'work with the deprecated method'.
                .signWith(getSecretKey())
                .compact();
    }

    public String getUsernameJwt(String token) {
        // Initial implementation using deprecated methods.
//        return Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();

        return Jwts.parser()
                // .setSigningKey(jwtSecret) // working with deprecated method
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwt(String authToken) {
        try {
            //Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(authToken); // working with deprecated method
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage(), e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage(), e);
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage(), e);
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage(), e);
        }
        return false;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}