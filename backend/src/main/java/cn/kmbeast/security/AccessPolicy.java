package cn.kmbeast.security;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.em.RoleEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Arrays;

public final class AccessPolicy {
    private AccessPolicy() { }
    public static Integer userId() {
        Integer id = LocalThreadHolder.getUserId();
        Integer role = LocalThreadHolder.getRoleId();
        if (id == null || id <= 0 || (role == null || RoleEnum.ROLE(role) == null)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        return id;
    }
    public static boolean isAdmin() {
        userId();
        return RoleEnum.ADMIN.getRole().equals(LocalThreadHolder.getRoleId());
    }
    public static void requireRoles(RoleEnum... roles) {
        userId();
        if (roles.length > 0 && Arrays.stream(roles).noneMatch(r -> r.getRole().equals(LocalThreadHolder.getRoleId()))) {
            forbidden();
        }
    }
    public static void forbidden() {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not permitted to access this resource");
    }
}
