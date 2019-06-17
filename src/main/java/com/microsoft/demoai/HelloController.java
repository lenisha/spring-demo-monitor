package com.microsoft.demoai;

import com.microsoft.applicationinsights.TelemetryClient;
import java.io.IOException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@RestController
@RequestMapping("/v1")
public class HelloController {
    private final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    TelemetryClient telemetryClient;
    private CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

    @GetMapping("/trackAuto")
    public int trackDependencyAutomatically() throws IOException {
        HttpGet httpGet = new HttpGet("https://www.google.com");
        int status;
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
            status = response.getStatusLine().getStatusCode();
        }
        return status;
    }

    @GetMapping("/track")
    public String hellotrace() {

        // track a custom event
        //telemetryClient.trackEvent("Sending a custom event...");

        // trace a custom trace
        //telemetryClient.trackTrace("Sending a custom trace....");

        double rand = new Random().nextDouble();
        // track a custom metric
        telemetryClient.trackMetric("custom business metric", rand);

        // track a custom dependency
        telemetryClient.trackDependency("ClientProfile Service", "Get", new Duration(0, 0, 1, 1, 1), true);

        return "hellotrace";
    }

    @GetMapping("/trackdim")
    public String hellodimension() {
        
        int rand = new Random().nextInt(10);
        // track custom dimension
        RequestTelemetry telemetry = new RequestTelemetry();
        telemetry.getProperties().put("team", "team" + rand);
        telemetry.getProperties().put("client", "client" + rand);

        telemetryClient.track(telemetry);

        return "hello";
    }

    @GetMapping("/trackex")
    public String trackexeption() {
        HelloUtils.trackexception();
        return "hello";
    }

    @GetMapping("/ex")
    public String unhandled() {
        throw new NullPointerException();
    }

     @GetMapping("/trackslow")
     public String trackslow() {
         HelloUtils.slow();
         return "hello slow";
     }

     @GetMapping("/slow")
     public String slow() {
         HelloUtils.slow();
         return "hello slow";
     }

     @GetMapping("/hello")
     public String hello() {
        log.info("info logback hello");
        return "hello";
     }
 }