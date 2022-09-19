package fi.roope.fmprojectbackend.api;

import fi.roope.fmprojectbackend.model.Route;
import fi.roope.fmprojectbackend.partialmodels.LikeRoutePatch;
import fi.roope.fmprojectbackend.partialmodels.PublicStatusPatch;
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

    @PatchMapping("/route/{id}")
    public ResponseEntity<?> updateRoutePublicStatus(@RequestBody PublicStatusPatch partialUpdate, @PathVariable Long id) {
        boolean saveOk = routeService.savePartial(partialUpdate, id);
        if (!saveOk) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("resource public status updated");
    }

    @PatchMapping("/route/like/{id}")
    public ResponseEntity<?> updateRouteLikes(@RequestBody LikeRoutePatch partialUpdate, @PathVariable Long id) {
        boolean saveOk = routeService.savePartialLikes(partialUpdate, id);
        if (!saveOk) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("resource public status updated");
    }
}
