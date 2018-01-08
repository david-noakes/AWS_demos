package au.gov.qld.idm.neo.identity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project qgcidmneoapplication
 * Created on 04 Jan 2018.
 */
@RestController
public class HealthController {

    @GetMapping(value = "/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("UP");
    }
}
