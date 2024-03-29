package fi.roope.fmprojectbackend.api;

import fi.roope.fmprojectbackend.model.Route;
import fi.roope.fmprojectbackend.partialmodels.AdminPatch;
import fi.roope.fmprojectbackend.partialmodels.LikeRoutePatch;
import fi.roope.fmprojectbackend.partialmodels.PublicStatusPatch;
import fi.roope.fmprojectbackend.partialmodels.RoutePatch;
import fi.roope.fmprojectbackend.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @GetMapping("/route/public")
    public ResponseEntity<List<Route>> getPublicRoutes() {
        return ResponseEntity.ok(routeService.getPublicRoutes());
    }

    @GetMapping("/route/admin")
    public ResponseEntity<List<Route>> getPublicAdminRoutes() {
        return ResponseEntity.ok(routeService.adminGetPublicRoutes());
    }

    @GetMapping("/route")
    public ResponseEntity<List<Route>> getOwnRoutes(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        return ResponseEntity.ok(routeService.getOwnRoutes(authHeader));
    }

    @PostMapping("/route")
    public ResponseEntity<Route> saveRoute(@RequestBody Route route) {
        var uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/route").toUriString());
        return ResponseEntity.created(uri).body(routeService.saveRoute(route));
    }

    @PatchMapping("/route/public-status/{id}")
    public ResponseEntity<?> updateRoutePublicStatus(@RequestBody PublicStatusPatch partialUpdate, @PathVariable Long id) {
        boolean saveOk = routeService.savePartial(partialUpdate, id);
        if (!saveOk) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("resource public status updated");
    }

    @PatchMapping("/route/{id}")
    public ResponseEntity<?> updateRouteMetaData(@RequestBody RoutePatch partialUpdate, @PathVariable Long id) {
        boolean saveOk = routeService.savePartialMeta(partialUpdate, id);
        if (!saveOk) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("resource meta updated");
    }

    @PatchMapping("/point/{id}")
    public ResponseEntity<?> updatePointMetaData(@RequestBody RoutePatch partialUpdate, @PathVariable Long id) {
        boolean saveOk = routeService.savePartialPoint(partialUpdate, id);
        if (!saveOk) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("resource meta updated");
    }

    @PatchMapping("/route/like/{id}")
    public ResponseEntity<?> updateRouteLikes(@RequestBody LikeRoutePatch partialUpdate, @PathVariable Long id) {
        boolean saveOk = routeService.savePartialLikes(partialUpdate, id);
        if (!saveOk) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("resource likes updated");
    }

    @PatchMapping("/route/admin-publish/{id}")
    public ResponseEntity<?> updateRouteDraft(@RequestBody AdminPatch partialUpdate, @PathVariable Long id) {
        boolean saveOk = routeService.savePartialAdminDraft(partialUpdate, id);
        if (!saveOk) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("resource updated");
    }

    @DeleteMapping("/route/delete/{id}")
    public ResponseEntity<?> deleteSingleRoute(@PathVariable Long id) {
        boolean saveOk = routeService.deleteIfExists(id);
        if (!saveOk) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("resource deleted");
    }
}
