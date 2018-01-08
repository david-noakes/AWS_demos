package au.gov.qld.idm.neo.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Project qgcidmneoapplication
 * Created on 04 Jan 2018.
 */
@SpringBootApplication
public class NeoApplication {

    private static final Logger LOG = LoggerFactory.getLogger(NeoApplication.class);

    public static void main(String[] args) {
        LOG.info("Starting CIDM Neo application");
        SpringApplication.run(NeoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initialise() {
        return args -> LOG.info(">>> INITIALISING APPLICATION <<<");
    }
}
