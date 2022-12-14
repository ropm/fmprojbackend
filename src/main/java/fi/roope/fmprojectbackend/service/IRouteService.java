package fi.roope.fmprojectbackend.service;

import fi.roope.fmprojectbackend.model.Route;
import fi.roope.fmprojectbackend.partialmodels.AdminPatch;
import fi.roope.fmprojectbackend.partialmodels.LikeRoutePatch;
import fi.roope.fmprojectbackend.partialmodels.PublicStatusPatch;
import fi.roope.fmprojectbackend.partialmodels.RoutePatch;

import java.util.List;

public interface IRouteService {
    Route saveRoute(Route route);
    boolean savePartial(PublicStatusPatch partialUpdate, Long id);

    boolean savePartialMeta(RoutePatch partialUpdate, Long id);

    boolean savePartialPoint(RoutePatch partialUpdate, Long id);

    List<Route> getPublicRoutes();
    List<Route> adminGetPublicRoutes();
    List<Route> getOwnRoutes(String authHeader);
    boolean savePartialLikes(LikeRoutePatch partialUpdate, Long id);
    boolean savePartialAdminDraft(AdminPatch partialUpdate, Long id);
    boolean deleteIfExists(Long id);
    void deleteAll();
}
