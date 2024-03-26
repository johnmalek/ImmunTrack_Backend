package com.ImmunTrackApp.ImmunTrackApp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
    HEALTHCARE(
            Set.of(
                    Permissions.HEALTHCARE_CREATE,
                    Permissions.HEALTHCARE_READ,
                    Permissions.HEALTHCARE_UPDATE,
                    Permissions.HEALTHCARE_DELETE,
                    Permissions.HEALTHCARE_PATCH

            )
    ),
    ADMIN(
            Set.of(
                    Permissions.ADMIN_READ,
                    Permissions.ADMIN_UPDATE,
                    Permissions.ADMIN_CREATE,
                    Permissions.ADMIN_DELETE,
                    Permissions.ADMIN_PATCH,
                    Permissions.HEALTHCARE_PATCH,
                    Permissions.HEALTHCARE_CREATE,
                    Permissions.HEALTHCARE_READ,
                    Permissions.HEALTHCARE_UPDATE,
                    Permissions.HEALTHCARE_DELETE

            )
    )
    ;

    @Getter
    private final Set<Permissions> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
