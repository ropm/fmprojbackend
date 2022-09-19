package fi.roope.fmprojectbackend.service;

import fi.roope.fmprojectbackend.model.AppRole;
import fi.roope.fmprojectbackend.model.AppUser;
import fi.roope.fmprojectbackend.repository.AppRoleRepository;
import fi.roope.fmprojectbackend.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static fi.roope.fmprojectbackend.util.AuthUtil.ACTIVATED;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppUserService implements IAppUserService, UserDetailsService {
    private final AppUserRepository userRepo;
    private final AppRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving user {}...", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user); //TODO: validations
    }

    @Override
    public AppRole saveRole(AppRole role) {
        log.info("Saving role {}...", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        var user = userRepo.findAppUserByUsername(username);
        var role = roleRepo.findAppRoleByName(roleName);
        // transactional auto-saves
        user.getRoles().add(role);
    }

    @Override
    public AppUser getUser(String username) {
        return userRepo.findAppUserByUsername(username);
    }

    @Override
    public String isExistingUser(String username, String visibleName) {
        AppUser existingByUser = userRepo.findAppUserByUsername(username);
        AppUser existingByVisibleName = userRepo.findAppUserByVisibleName(visibleName);
        if (existingByUser != null && existingByVisibleName != null) {
            return "BOTH";
        } else if (existingByUser != null) {
            return "USERNAME";
        } else if (existingByVisibleName != null) {
            return "NAME";
        }
        return null;
    }

    @Override
    public List<AppUser> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepo.findAppUserByUsername(username);
        if (user == null) {
            log.error("User not found!");
            throw new UsernameNotFoundException("User not found!");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public boolean activate(String username, UUID uuid) {
        var user = userRepo.findAppUserByUsername(username);
        if (user == null) {
            log.error("User not found!");
            return false;
        }
        if (uuid == null || !uuid.equals(user.getActivationId())) {
            return false;
        }
        var role = roleRepo.findAppRoleByName(ACTIVATED);
        // transactional auto-saves
        user.getRoles().add(role);
        return true;
    }
}
