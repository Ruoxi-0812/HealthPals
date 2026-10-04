package cn.kmbeast.security;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.time.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
class AuthRateLimitFilterTest {
    @Test void concurrentAttemptsCannotExceedBudget() throws Exception {
        AuthRateLimitFilter filter=new AuthRateLimitFilter(Clock.fixed(Instant.EPOCH,ZoneOffset.UTC));
        ExecutorService executor=Executors.newFixedThreadPool(8);
        AtomicInteger accepted=new AtomicInteger();
        try {
            java.util.List<Future<?>> tasks=new java.util.ArrayList<>();
            for(int i=0;i<100;i++) tasks.add(executor.submit(()->{if(filter.allow("peer")) accepted.incrementAndGet();}));
            for(Future<?> task:tasks) task.get();
            assertEquals(20,accepted.get());
        } finally {executor.shutdownNow();}
    }
    @Test void budgetExpiresAndGlobalBudgetBoundsUniquePeers() {
        final long[] now={0};
        Clock clock=new Clock(){public ZoneId getZone(){return ZoneOffset.UTC;} public Clock withZone(ZoneId z){return this;} public Instant instant(){return Instant.ofEpochMilli(now[0]);}};
        AuthRateLimitFilter filter=new AuthRateLimitFilter(clock);
        for(int i=0;i<120;i++) assertTrue(filter.allow("peer"+i));
        assertFalse(filter.allow("another"));now[0]=60000;assertTrue(filter.allow("another"));
    }
    @Test void returns429AndRetryAfterWithoutTrustingForwardedHeaders() throws Exception {
        AuthRateLimitFilter filter=new AuthRateLimitFilter(Clock.fixed(Instant.EPOCH,ZoneOffset.UTC));
        for(int i=0;i<21;i++) {
            MockHttpServletRequest req=new MockHttpServletRequest("POST","/api/user/login");
            req.setContextPath("/api"); req.setRemoteAddr("proxy"); req.addHeader("X-Forwarded-For","fake"+i);
            MockHttpServletResponse res=new MockHttpServletResponse();
            filter.doFilter(req,res,(a,b)->b.getWriter().write("allowed"));
            assertEquals(i<20?200:429,res.getStatus());
            if(i==20){assertEquals("60",res.getHeader("Retry-After"));assertTrue(res.getContentAsString().contains("429"));}
        }
        MockHttpServletResponse health=new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET","/health/ready"),health,(a,b)->b.getWriter().write("ready"));
        assertEquals("ready",health.getContentAsString());
    }
}
