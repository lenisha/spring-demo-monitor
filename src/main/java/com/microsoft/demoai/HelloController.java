package com.microsoft.demoai;

 import com.microsoft.applicationinsights.TelemetryClient;
 import java.io.IOException;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;
 import com.microsoft.applicationinsights.telemetry.Duration;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;

 @RestController
 @RequestMapping("/sample")
 public class HelloController {
     private static final Logger log = LogManager.getLogger(HelloController.class);

     @Autowired
     TelemetryClient telemetryClient;

     @GetMapping("/hellotrace")
     public String hellotrace() {

         //track a custom event  
         telemetryClient.trackEvent("Sending a custom event...");

         //trace a custom trace
         telemetryClient.trackTrace("Sending a custom trace....");

         //track a custom metric
         telemetryClient.trackMetric("custom metric", 1.0);

         //track a custom dependency
         telemetryClient.trackDependency("SQL", "Insert", new Duration(0, 0, 1, 1, 1), true);

         return "hellotrace";
     }

     @GetMapping("/hello")
     public String hello() {
        log.info("log4j info hello");
        return "hello";
     }
 }