package uk.co.inhealthcare.open;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import uk.co.inhealthcare.open.jsat.OpenInhealthcareRouterConfig;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@Import(OpenInhealthcareRouterConfig.class)
@ImportResource("mybatis-context.xml")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
