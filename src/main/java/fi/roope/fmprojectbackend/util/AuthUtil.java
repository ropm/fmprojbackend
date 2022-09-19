package fi.roope.fmprojectbackend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.roope.fmprojectbackend.model.AppRole;
import fi.roope.fmprojectbackend.model.AppUser;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class AuthUtil {

    private AuthUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final int ACCESS_EXP = 10 * 60 * 1000;
    private static final int REFRESH_EXP = 60 * 60 * 1000;

    public static final String ADMIN = "ROLE_ADMIN";
    //private static final String CREATE = "ROLE_CREATE";
    public static final String VIEW = "ROLE_VIEW";
    public static final String ACTIVATED = "ROLE_USER_ACTIVATED";

    public static User getSpringUser(Authentication authResult) {
        return (User) authResult.getPrincipal();
    }

    public static Algorithm getAlgorithm() {
        return Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8));
    }

    public static String createAccessToken(HttpServletRequest request, Authentication authResult) {
        var springUser = getSpringUser(authResult);
        var algorithm = getAlgorithm();
        return JWT.create()
                .withSubject(springUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", springUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public static String createAccessTokenForAppUser(HttpServletRequest request, AppUser user) {
        var algorithm = getAlgorithm();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getRoles().stream().map(AppRole::getName).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public static String createRefreshToken(HttpServletRequest request, Authentication authResult) {
        var springUser = getSpringUser(authResult);
        var algorithm = getAlgorithm();
        return JWT.create()
                .withSubject(springUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
    }

    public static DecodedJWT verifyJWT(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        return verifier.verify(token);
    }

    public static void createTokenResponse(HttpServletResponse response, String access, String refresh) throws IOException {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", access);
        tokens.put("refresh", refresh);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    public static void createForbiddenResponse(HttpServletResponse response, Exception e) throws IOException {
        Map<String, String> resp = new HashMap<>();
        resp.put("Error", e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        new ObjectMapper().writeValue(response.getOutputStream(), resp);
    }

}
