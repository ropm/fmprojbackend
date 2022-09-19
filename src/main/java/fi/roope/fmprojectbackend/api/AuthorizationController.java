package fi.roope.fmprojectbackend.api;

import fi.roope.fmprojectbackend.service.AppUserService;
import fi.roope.fmprojectbackend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AppUserService userService;

    @PostMapping("/auth/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var refresh = authHeader.substring("Bearer ".length());
            try {
                var decodedJWT = AuthUtil.verifyJWT(refresh);
                var username = decodedJWT.getSubject();
                var user = userService.getUser(username);
                var access = AuthUtil.createAccessTokenForAppUser(request, user);
                AuthUtil.createTokenResponse(response, access, refresh);
            } catch (Exception e) {
                AuthUtil.createForbiddenResponse(response, e);
            }
        }
    }

}
