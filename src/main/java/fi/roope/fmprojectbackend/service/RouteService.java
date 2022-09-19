package fi.roope.fmprojectbackend.service;

import fi.roope.fmprojectbackend.model.Route;
import fi.roope.fmprojectbackend.partialmodels.LikeRoutePatch;
import fi.roope.fmprojectbackend.partialmodels.PublicStatusPatch;
import fi.roope.fmprojectbackend.repository.RouteRepository;
import fi.roope.fmprojectbackend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RouteService implements IRouteService {
    private final RouteRepository routeRepository;
    @Override
    public Route saveRoute(Route route) {
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
    public List<Route> getPublicRoutes() {
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
}
