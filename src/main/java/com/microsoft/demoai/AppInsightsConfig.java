package com.microsoft.demoai;

import java.util.Map;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.context.properties.*;
import com.microsoft.applicationinsights.extensibility.*;
import com.microsoft.applicationinsights.profiler.modules.*;

@Configuration
@EnableConfigurationProperties(ProfilerConfiguration.class)
public class AppInsightsConfig {
    private final ProfilerConfiguration profilerProperties;

    @Autowired
    public AppInsightsConfig(ProfilerConfiguration profilerProperties) {
        this.profilerProperties = profilerProperties;
    }

    @Bean
    public TelemetryModule profilerTelemetryModule() {
        return new ProfilerTelemetryModule(profilerProperties.toMap());
    }
}