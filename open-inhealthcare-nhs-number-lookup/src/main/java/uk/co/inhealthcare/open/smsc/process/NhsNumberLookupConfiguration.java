package uk.co.inhealthcare.open.smsc.process;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:nhs-number-lookup.xml")
public class NhsNumberLookupConfiguration {

}
