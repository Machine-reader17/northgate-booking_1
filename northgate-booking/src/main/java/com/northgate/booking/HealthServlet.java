package com.northgate.booking;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GET /health returns 200 with the app version. Use it for load balancer health checks,
 * Kubernetes liveness/readiness probes and uptime monitors.
 */
@WebServlet("/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String version = getClass().getPackage().getImplementationVersion();
        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"UP\",\"version\":\"" + (version == null ? "dev" : version) + "\"}");
    }
}
