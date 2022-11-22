package fi.roope.fmprojectbackend.filter;

import fi.roope.fmprojectbackend.util.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String currentPath = request.getServletPath();
        if (currentPath.equals("/api/v1/login") || currentPath.equals("/api/v1/auth/refresh") ||
                currentPath.equals("/api/v1/route/public") || currentPath.equals("/api/v1/register")) {
            // pass request forward in the chain
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.substring("Bearer ".length());
            try {
                var decodedJWT = AuthUtil.verifyJWT(token);
                var username = decodedJWT.getSubject();
                List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                AuthUtil.createForbiddenResponse(response, e);
            }

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
