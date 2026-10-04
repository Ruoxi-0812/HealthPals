package cn.kmbeast.Interceptor;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.security.AccessPolicy;
import cn.kmbeast.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LocalThreadHolder.clear();
        String method = request.getMethod();
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if ("OPTIONS".equals(method)) return true;
        // Spring Boot forwards uncaught exceptions from any handler to this internal path
        // (same request, same method). Without this, every backend 500 gets masked as a
        // generic 401 "Authentication required" here instead of surfacing the real error.
        if ("/error".equals(path)) return true;
        if ("GET".equals(method) && ("/health".equals(path) || "/health/ready".equals(path) || "/file/getFile".equals(path))) return true;
        if ("POST".equals(method) && ("/user/login".equals(path)
                || "/user/google-login".equals(path) || "/user/register".equals(path))) return true;
        Claims claims = JwtUtil.fromToken(request.getHeader("token"));
        Integer userId = null;
        Integer roleId = null;
        if (claims != null) {
            try {
                userId = claims.get("id", Integer.class);
                roleId = claims.get("role", Integer.class);
            } catch (RuntimeException ignored) { /* Invalid identity claims must fail closed. */ }
        }
        if (userId == null || userId <= 0 || roleId == null || RoleEnum.ROLE(roleId) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Authentication required\"}");
            return false;
        }
        LocalThreadHolder.setUserId(userId, roleId);
        if (handler instanceof HandlerMethod) {
            Protector protector = ((HandlerMethod) handler).getMethodAnnotation(Protector.class);
            try {
                if (protector != null) AccessPolicy.requireRoles(protector.roles());
            } catch (RuntimeException error) {
                LocalThreadHolder.clear();
                throw error;
            }
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        LocalThreadHolder.clear();
    }
}
