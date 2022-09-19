package fi.roope.fmprojectbackend.repository;

import fi.roope.fmprojectbackend.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findAllByPublicVisibilityOrderById(boolean visibility);
    List<Route> findAllByCreatorOrderById(String creator);
}
