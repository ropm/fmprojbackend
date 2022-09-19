package fi.roope.fmprojectbackend.partialmodels;

import lombok.Data;

@Data
public class PublicStatusPatch {
    private Long id;
    private boolean publicVisibility;
}
