package com.metricas_monitoreo_data_center_iot.com.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metricas_monitoreo_data_center_iot.com.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.persistence.repository.UsuarioRepository;
import com.metricas_monitoreo_data_center_iot.com.security.service.CustomUserDetailsService;
import com.metricas_monitoreo_data_center_iot.com.web.controller.AutenticationController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AutenticationController.class);
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String origin = request.getHeader("Origin");
        log.info("Request: {} {} from Origin: {}", request.getMethod(), requestPath, origin);
        log.info("Headers: {}", Collections.list(request.getHeaderNames()));

        if (requestPath.contains("/auth/")) {
            log.debug("Ruta de auth, pasando al controller...");
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if(token != null) {
            try {
                if (!jwtUtil.validateToken(token)){
                    filterChain.doFilter(request, response);
                    return;
                }

                String username = jwtUtil.extractUsername(token);
                UsuarioEntity usuario = usuarioRepository.findByCorreo(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                if(!jwtUtil.isTokenVersionValid(token, usuario.getTokenVersion())){
                    sendErrorResponse(response, "Sesión expirada. Por favor, inicie sesión nuevamente.");
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                logger.error("Error en la autenticacion: {}", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("timestamp", new Date());

        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
