package fi.roope.fmprojectbackend.repository;

import fi.roope.fmprojectbackend.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findAppRoleByName(String name);
}
