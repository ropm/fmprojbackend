package fi.roope.fmprojectbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Long likes;
    private boolean isDraft;
    private boolean published;
    private boolean publicVisibility;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<RoutePoint> points = new ArrayList<>();
    private String creator;
}
