package cn.kmbeast.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/** In-memory per-peer budget plus a process-wide cap, before expensive password/token verification.
 * Never trust arbitrary forwarded headers. Behind shared proxies the peer limit is shared.
 * Limits reset on restart and are not distributed across replicas.
 */
@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {
    private final Clock clock;
    private final Map<String, Integer> attempts = new HashMap<>();
    private long window = -1;
    private int total;
    public AuthRateLimitFilter() { this(Clock.systemUTC()); }
    AuthRateLimitFilter(Clock clock) { this.clock = clock; }
    synchronized boolean allow(String peer) {
        long now = clock.millis() / 60000;
        if (window != now) { window = now; attempts.clear(); total = 0; }
        if (total >= 120 || attempts.getOrDefault(peer, 0) >= 20) return false;
        total++;
        attempts.put(peer, attempts.getOrDefault(peer, 0) + 1);
        return true;
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean auth = "/user/login".equals(path) || "/user/google-login".equals(path) || "/user/register".equals(path) || "/user/updatePwd".equals(path);
        if (auth && ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) && !allow(request.getRemoteAddr())) {
            response.setStatus(429);
            response.setHeader("Retry-After", Long.toString(60 - (clock.millis() / 1000) % 60));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"msg\":\"Too many authentication attempts. Please try again shortly.\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}
