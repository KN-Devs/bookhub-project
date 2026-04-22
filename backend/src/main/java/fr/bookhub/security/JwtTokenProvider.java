package fr.bookhub.security;



import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:mySecretKeyForBookHubApplicationThatIsAtleast32CharactersLongForHS256Algorithm}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 heures par défaut
    private int jwtExpirationMs;

    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateTokenFromEmail(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: {}" + e);
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: {}" + e);
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token: {}" + e);
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token: {}" + e);
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: {}" + e);
        }
        return false;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
