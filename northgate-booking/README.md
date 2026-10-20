# Northgate Booking

The online booking app for **Northgate Physiotherapy Ltd** (a fictional client). You're the DevOps
engineer taking it from a developer's laptop to production as part of the LearnWithPride DevOps
course. Your tickets are on the platform.

## What's here

| Path | What it is |
|---|---|
| `pom.xml` | Maven build: Java 17, packages a WAR, runs JUnit tests, JaCoCo coverage report |
| `src/main/java/...` | The booking rules (`BookingService`) and two endpoints |
| `src/main/webapp/index.html` | A simple booking page |
| `src/test/java/...` | Unit tests |

## Build

You need **Java 17** and **Maven 3.9+**.

```bash
mvn clean verify
```

This compiles, runs the tests, builds `target/booking.war` and writes a coverage report to
`target/site/jacoco/index.html`.

## Run

Deploy `target/booking.war` to **Tomcat 10.1 or later** (it uses Jakarta Servlet 6). Then open:

| URL | Returns |
|---|---|
| `http://<host>:8080/booking/` | The booking page |
| `http://<host>:8080/booking/health` | `{"status":"UP","version":"..."}`: use for health checks and probes |
| `http://<host>:8080/booking/api/slots?clinic=Leeds` | Free appointment slots as JSON |

## Your job

There's no Jenkinsfile, Dockerfile, Kubernetes manifest or Terraform here yet: adding those is
your work on the course. Keep secrets (passwords, tokens, keys) **out of this repository**.
