package fi.roope.fmprojectbackend.repository;

import fi.roope.fmprojectbackend.model.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
}
