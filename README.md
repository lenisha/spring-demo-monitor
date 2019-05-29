## Getting Started with SpringBoot Logging and App Insights on Azure AppService (Windows)

This repo has demonstrates using App Insights Logging Appenders for both `log4j` and `logback` logging frameworks.

### LogBack
- Add required libraries to enable integratiob with App Insights

```
        <dependency>
			<groupId>com.microsoft.azure</groupId>
			<artifactId>applicationinsights-spring-boot-starter</artifactId>
			<version>1.1.2</version>
		</dependency>
 		<dependency>
            <groupId>com.microsoft.azure</groupId>
            <artifactId>applicationinsights-logging-logback</artifactId>
            <version>2.3.1</version>
        </dependency>
```

where `applicationinsights-spring-boot-starter` is enabling telemetry data to flow to AppInsights,
and `applicationinsights-logging-logback` is providing LogBack AppInsights appender.
Logback is default Logging framework for SpringBoot and is part of spring web starter.

- Add AI instrumentation keys to `application.properties`

```
# Specify the instrumentation key of your Application Insights resource.
azure.application-insights.instrumentation-key=[ key ]
```

- Add AppInsights Appender to logback config , typically `logback-spring.xml` file under  `main\resources`
instrumentation key would be sourced from spring boot properties

```
<configuration scan="true">
    <include resource="org/springframework/boot/logging/logback/base.xml"/>

    <springProperty scope="context" name="INSTRUMENTATION_KEY"
                    source="azure.application-insights.instrumentation-key"/>

    <appender name="aiAppender"
              class="com.microsoft.applicationinsights.logback.ApplicationInsightsAppender">
        <instrumentationKey>${INSTRUMENTATION_KEY}</instrumentationKey>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="aiAppender" />
        <appender-ref ref="FILE" />
        <appender-ref ref="CONSOLE" />
    </root>
</Configuration>    
```

- Add `web.config` that is used to start SpringBoot JAR in Azure App Service
Environment variable `logging.file` is used by default by `FILE` appender using by springboot default config

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <system.webServer>
    <handlers>
      <remove name="httpPlatformHandlerMain" />
      <add name="httpPlatformHandlerMain" path="*" verb="*" modules="httpPlatformHandler" resourceType="Unspecified"/>
    </handlers>
    <httpPlatform processPath="%JAVA_HOME%\bin\java.exe"
            arguments="-Djava.net.preferIPv4Stack=true -Dserver.port=%HTTP_PLATFORM_PORT% -Dlogging.file=&quot;%HOME%\LogFiles\bootlogback.log&quot; -jar &quot;D:\home\site\wwwroot\demoai-0.0.1-SNAPSHOT.jar&quot;">
    </httpPlatform>
  </system.webServer>
</configuration>
```

### Log4J

Refer to `log4j` branch to see the config, it requires a bit more config comparing to logback

- add required libraries to `pom.xml`, exclude default Logback injected in spring starter and include log4j specific starter.
```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
        </exclusions>	
	</dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>

    <dependency>
        <groupId>com.microsoft.azure</groupId>
        <artifactId>applicationinsights-spring-boot-starter</artifactId>
        <version>1.1.2</version>
    </dependency>
	
    <dependency>
        <groupId>com.microsoft.azure</groupId>
        <artifactId>applicationinsights-logging-log4j2</artifactId>
        <version>2.3.1</version>
    </dependency>
```

- where `applicationinsights-spring-boot-starter` is enabling telemetry data to flow to AppInsights,
and `applicationinsights-logging-log4j2` is providing Log4j AppInsights appender.

- Add AI instrumentation keys to `application.properties`
```
# Specify the instrumentation key of your Application Insights resource.
azure.application-insights.instrumentation-key=[ key ]
```
- Add AppInsights Appender to Log4j2 config , typically `log4j2-spring.xml` file under  `main\resources`

```
<Configuration packages="com.microsoft.applicationinsights.log4j.v2">
  <Properties>
    <Property name="LOG_PATTERN">
      %d{yyyy-MM-dd HH:mm:ss.SSS} %5p ${hostName} --- [%15.15t] %-40.40c{1.} : %m%n%ex
    </Property>
  </Properties>
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
    <ApplicationInsightsAppender name="aiAppender">
    </ApplicationInsightsAppender>
     <File name="MyFile" fileName="${sys:logging.file:-boot.log}">
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
      </PatternLayout>
    </File>
    <Async name="Async">
      <AppenderRef ref="MyFile"/>
    </Async>
  </Appenders>
  <Loggers>
    <Root level="info">
      <AppenderRef ref="Console"  />
      <AppenderRef ref="aiAppender"  />
      <AppenderRef ref="Async"  />
    </Root>
  </Loggers>
</Configuration>
```

- Use same `web.config` as described above, to make sure File output makes use of environment variable `logging.file` set  `fileName="${sys:logging.file:-boot.log}` for the appender as shown above.


## Deploy using Maven plugin

See `pom.xml` for setting up the latest plugin and run
`mvn clean package azure-webapp:deploy`


### Guides
The following guides illustrate how to use some features concretely:
* [Configure a Spring Boot Initializer app to use Application Insights](https://docs.microsoft.com/en-us/java/azure/spring-framework/configure-spring-boot-java-applicationinsights?view=azure-java-stable)

* [How to use Micrometer with Azure Monitor](https://docs.microsoft.com/en-us/azure/azure-monitor/app/micrometer-java)



