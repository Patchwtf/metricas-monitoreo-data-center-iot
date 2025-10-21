package com.metricas_monitoreo_data_center_iot.com.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.default:3600000}")
    private Long defaultExpiration;

    @Value("${jwt.expiration.admin:1800000}")
    private Long adminExpiration;

    @Value("${jwt.expiration.servicio:7200000}")
    private Long servicioExpiration;

    @Value("${jwt.expiration.usuario:86400000}")
    private Long usuarioExpiration;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, List<String> roles, Integer tokenVersion) {
        long expirationMs = getExpirationForRoles(roles);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("version", tokenVersion)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Integer extractTokenVersion(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("version", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenVersionValid(String token, Integer currentUserVersion) {
        try {
            Integer tokenVersion = extractTokenVersion(token);
            return tokenVersion != null && tokenVersion.equals(currentUserVersion);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenFullyValid(String token, Integer currentUserVersion) {
        return validateToken(token) && isTokenVersionValid(token, currentUserVersion);
    }

    private long getExpirationForRoles(List<String> roles) {
        if (roles.contains("ADMIN")) {
            return adminExpiration;
        } else if (roles.contains("SERVICIO")) {
            return servicioExpiration;
        } else {
            return usuarioExpiration;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object rolesObj = claims.get("roles");

            if (rolesObj instanceof List<?> rawList) {
                if (rawList.stream().allMatch(item -> item instanceof String)) {
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) rawList;
                    return roles;
                } else {
                    return rawList.stream()
                            .map(Object::toString)
                            .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener expiración del token", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isTokenValid(String token) {
        return validateToken(token) && !isTokenExpired(token);
    }

}
