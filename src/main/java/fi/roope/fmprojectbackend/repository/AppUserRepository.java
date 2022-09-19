package fi.roope.fmprojectbackend.repository;

import fi.roope.fmprojectbackend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByUsername(String username);
    AppUser findAppUserByVisibleName(String visibleName);
}
