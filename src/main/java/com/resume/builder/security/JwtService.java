package com.resume.builder.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long expirationMs;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expirationMs}") long expirationMs
  ) {
    // HS256 signing key derived from secret; secret should be sufficiently long for HMAC.
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String createToken(Long userId, String email, String role) {
    Instant now = Instant.now();
    Instant exp = now.plusMillis(expirationMs);

    return Jwts.builder()
        .setSubject(userId.toString())
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .claim("email", email)
        .claim("role", role)
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public Long extractUserId(String token) {
    Claims claims = parseClaims(token);
    return Long.parseLong(claims.getSubject());
  }

  public String extractEmail(String token) {
    Claims claims = parseClaims(token);
    Object email = claims.get("email");
    return email == null ? "" : email.toString();
  }

  public String extractRole(String token) {
    Claims claims = parseClaims(token);
    Object role = claims.get("role");
    return role == null ? "USER" : role.toString();
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

