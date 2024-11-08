package hh.ont.shiftbooking.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import hh.ont.shiftbooking.model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtService {

    @Value("${jwt.jwtsecret}")
    private String jwtSecret;
    @Value("${jwt.jwtExpirationTime}")
    private Long jwtExpirationTime;
    private static final String BEARER = "Bearer ";

    /**
     * Luo JSON Web Tokenin.
     * @param user Todennettu käyttäjä User-objektina
     * @return JSON Web Token
     */
    public String createToken(User user) {
        return Jwts.builder()
            .header().add("typ", "JWT").and()
            .subject(user.getUsername())
            .claim("role", user.getAuthorities())
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
            .signWith(getKey())
            .compact();
    }

    public SecretKey getKey() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        return key;
    }

    /**
     * Poimii JSON Web Tokenin sisään tulleesta pyynnöstä.
     * @param request Sisään tullut http-pyyntö
     * @return JSON Web Token
     */
    public String getToken(HttpServletRequest request) {

        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        return (bearerToken != null && bearerToken.startsWith(BEARER)) ?
            bearerToken.substring(7, bearerToken.length()) :
            null;
    }

    /**
     * Purkaa parametrina saadusta tokenista käyttäjätunnuksen.
     * @param token JSON Web Token
     * @return Käyttäjätunnus
     */
    public String validateToken(String token) {
        String username = "";

        try {
            username = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        } catch (JwtException e) { }
        return username;
    }

    /**
     * Tarkastaa, että parametrina saatu token on voimassa.
     * @param token JSON Web Token
     * @return Tiedon siitä, onko token voimassa (true/false)
     */
    public boolean isTokenExpired(String token) {
        Date exp = null;

        try {
            exp = Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration();
        } catch (JwtException e) { }
        return exp.before(new Date());
    }
}
