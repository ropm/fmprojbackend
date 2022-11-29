package fi.roope.fmprojectbackend.service;

import fi.roope.fmprojectbackend.model.Route;
import fi.roope.fmprojectbackend.model.RoutePoint;
import fi.roope.fmprojectbackend.partialmodels.AdminPatch;
import fi.roope.fmprojectbackend.partialmodels.LikeRoutePatch;
import fi.roope.fmprojectbackend.partialmodels.PublicStatusPatch;
import fi.roope.fmprojectbackend.partialmodels.RoutePatch;
import fi.roope.fmprojectbackend.repository.RoutePointRepository;
import fi.roope.fmprojectbackend.repository.RouteRepository;
import fi.roope.fmprojectbackend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RouteService implements IRouteService {
    private final RouteRepository routeRepository;
    private final RoutePointRepository routePointRepository;

    @Override
    public Route saveRoute(Route route) {
        Collection<RoutePoint> pointList = route.getPoints();
        routePointRepository.saveAll(pointList);
        return routeRepository.save(route);
    }

    @Override
    public boolean savePartial(PublicStatusPatch partialUpdate, Long id) {
        Route existingRoute = routeRepository.findById(id).orElse(null);
        if (existingRoute != null) {
            existingRoute.setPublicVisibility(partialUpdate.isPublicVisibility());
            routeRepository.save(existingRoute);
            return true;
        }
        return false;
    }

    @Override
    public boolean savePartialMeta(RoutePatch partialUpdate, Long id) {
        Route existingRoute = routeRepository.findById(id).orElse(null);
        if (existingRoute != null) {
            existingRoute.setName(partialUpdate.getName());
            existingRoute.setDescription(partialUpdate.getDescription());
            routeRepository.save(existingRoute);
            return true;
        }
        return false;
    }

    @Override
    public boolean savePartialPoint(RoutePatch partialUpdate, Long id) {
        RoutePoint existingPoint = routePointRepository.findById(id).orElse(null);
        if (existingPoint != null) {
            existingPoint.setName(partialUpdate.getName());
            existingPoint.setDescription(partialUpdate.getDescription());
            routePointRepository.save(existingPoint);
            return true;
        }
        return false;
    }

    @Override
    public List<Route> getPublicRoutes() {
        // haetaan reitit jotka on julkaistu (admin hyväksynyt) ja ovat muuten julkisia
        return routeRepository.findAllByPublicVisibilityAndPublishedOrderById(true, true);
    }

    @Override
    public List<Route> adminGetPublicRoutes() {
        // haetaan reitit, jotka näkyy kaikille (adminin pitää hyväksyä (published = true))
        return routeRepository.findAllByPublicVisibilityOrderById(true);
    }

    @Override
    public List<Route> getOwnRoutes(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.substring("Bearer ".length());
            try {
                var decodedJWT = AuthUtil.verifyJWT(token);
                var username = decodedJWT.getSubject();
                return routeRepository.findAllByCreatorOrderById(username);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean savePartialLikes(LikeRoutePatch partialUpdate, Long id) {
        Route existingRoute = routeRepository.findById(id).orElse(null);
        if (existingRoute != null) {
            existingRoute.setLikes(existingRoute.getLikes() + partialUpdate.getLikes());
            routeRepository.save(existingRoute);
            return true;
        }
        return false;
    }

    @Override
    public boolean savePartialAdminDraft(AdminPatch partialUpdate, Long id) {
        Route existingRoute = routeRepository.findById(id).orElse(null);
        if (existingRoute != null) {
            existingRoute.setPublished(partialUpdate.isPublished());
            routeRepository.save(existingRoute);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteIfExists(Long id) {
        Route existingRoute = routeRepository.findById(id).orElse(null);
        if (existingRoute != null) {
            routeRepository.delete(existingRoute);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAll() {
        routeRepository.deleteAll();
    }
}
