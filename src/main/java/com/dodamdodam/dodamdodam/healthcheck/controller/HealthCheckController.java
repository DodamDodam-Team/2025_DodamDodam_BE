package com.dodamdodam.dodamdodam.healthcheck.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

@RestController
public class HealthCheckController {

    @GetMapping("/healthcheck")
    public ResponseEntity<Map<String, String>> healthcheck() {
        return ResponseEntity.ok(Collections.singletonMap("status", "ok"));
    }

}
