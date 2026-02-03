package com.mini_crm.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.mini_crm.main.service.SseService;

@RestController
@RequestMapping("/api/sse")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SseController {

    @Autowired
    private SseService sseService;

    @GetMapping(value = "/{userId}/subscribe", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleSse(@PathVariable Long userId) {
        return sseService.subscribe(userId);
    }
}
