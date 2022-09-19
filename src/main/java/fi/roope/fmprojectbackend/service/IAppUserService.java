package fi.roope.fmprojectbackend.service;

import fi.roope.fmprojectbackend.model.AppRole;
import fi.roope.fmprojectbackend.model.AppUser;

import java.util.List;
import java.util.UUID;

public interface IAppUserService {
    AppUser saveUser(AppUser user);
    AppRole saveRole(AppRole role);
    void addRoleToUser(String username, String roleName);
    AppUser getUser(String username);

    String isExistingUser(String username, String visibleName);

    List<AppUser> getUsers();

    boolean activate(String username, UUID uuid);
}
