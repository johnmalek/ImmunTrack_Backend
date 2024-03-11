package com.ImmunTrackApp.ImmunTrackApp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permissions {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    HEALTHCARE_READ("healthcare:read"),
    HEALTHCARE_UPDATE("healthcare:update"),
    HEALTHCARE_CREATE("healthcare:create"),
    HEALTHCARE_DELETE("healthcare:delete")

    ;

    @Getter
    private final String permission;
}
