package com.Cloudy.Cloudy_Self_Checkout_POS.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTUtil {

    private String secretKey = "lucia1$";

    public String extractUsername(String token){return extractClaims(token, Claims::getSubject);}

    //    set expiration time
    public Date extractExpiration(String token){
        return  extractClaims(token,Claims::getExpiration);
    }

    public<T> T extractClaims(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    // Extract all claims without manually Base64 decoding
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
    //    check expiration
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private String createToken(Map<String, Object> claims, String subject) {
        // Ensure the secret is properly converted to a byte[] if needed
        byte[] signingKey = secretKey.getBytes();

        // Return the compacted JWT token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiration
                .signWith(SignatureAlgorithm.HS256, signingKey) // Sign with HS256 algorithm
                .compact();
    }


    public String generateToken(String username,String role,String avatar){
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",role);
        claims.put("avatar",avatar);
        return createToken(claims,username);
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role"); // Extract the "role" claim
    }
    public String extractAvatar(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("avatar"); // Extract the "avatar" claim
    }
}
