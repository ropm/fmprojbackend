package fi.roope.fmprojectbackend.api;

import fi.roope.fmprojectbackend.model.AppRole;
import fi.roope.fmprojectbackend.model.AppUser;
import fi.roope.fmprojectbackend.model.UserRoleForm;
import fi.roope.fmprojectbackend.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fi.roope.fmprojectbackend.util.AuthUtil.ACTIVATED;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        var uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/role/save")
    public ResponseEntity<AppRole> saveRole(@RequestBody AppRole role) {
        var uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/save-role").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/add")
    public ResponseEntity<?> addRoleToUser(@RequestBody UserRoleForm userRoleForm) {
        userService.addRoleToUser(userRoleForm.getUsername(), userRoleForm.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(HttpServletRequest request, @RequestBody AppUser user) {
        String message = "Tunnus luotu, tarkista sähköpostistasi tunnuksen vahvistuslinkki.";
        String existingCode = userService.isExistingUser(user.getUsername(), user.getVisibleName());
        if (existingCode != null) {
            switch (existingCode) {
                case "BOTH" -> message = "Sähköposti ja tunnus ovat jo rekisteröity.";
                case "USERNAME" -> message = "Sähköposti on jo rekisteröity.";
                case "NAME" -> message = "Tunnus on jo rekisteröity.";
                default -> message = "Tarkista syötetyt tiedot.";
            }
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        try {
            user.setActivationId(UUID.randomUUID());
            AppUser created = userService.saveUser(user);
            // TODO: activation pois tästä kun aktivointi toimii
            userService.addRoleToUser(user.getUsername(), ACTIVATED);
            UriComponents uriComponents = ServletUriComponentsBuilder
                    .fromRequest(request)
                    .replacePath("/activate")
                    .queryParam("user", created.getUsername())
                    .queryParam("uuid", created.getActivationId())
                    .build();
            String activationUrl = String.valueOf(uriComponents.toUri());
            // TODO: send mail to created.username with username + activationId
        } catch (Exception e) {
            return new ResponseEntity<>("Tarkista syötetyt tiedot.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateUser(@RequestParam String user, @RequestParam UUID uuid) {
        boolean ok = userService.activate(user, uuid);
        if (!ok) {
            return new ResponseEntity<>("Tapahtui virhe aktivoinnissa!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Aktivoitu", HttpStatus.OK);
    }
}
