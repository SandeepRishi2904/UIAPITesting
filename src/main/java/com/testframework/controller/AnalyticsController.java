package com.testframework.controller;

import com.testframework.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/trends/{suiteId}")
    public ResponseEntity<List<Map<String, Object>>> getSuiteTrends(
            @PathVariable Long suiteId,
            @RequestParam(defaultValue = "30") int days) {
        List<Map<String, Object>> trends = analyticsService.getExecutionTrends(suiteId, days);
        return ResponseEntity.ok(trends);
    }
}