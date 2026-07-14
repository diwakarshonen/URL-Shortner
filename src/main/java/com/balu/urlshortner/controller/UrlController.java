package com.balu.urlshortner.controller;

import com.balu.urlshortner.dto.ShortenRequest;
import com.balu.urlshortner.dto.ShortenResponse;
import com.balu.urlshortner.entity.UrlMapping;
import com.balu.urlshortner.repository.UrlRepository;
import com.balu.urlshortner.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlService urlService;
    private final UrlRepository urlRepository;

    public UrlController(UrlService urlService, UrlRepository urlRepository) {
        this.urlService = urlService;
        this.urlRepository = urlRepository;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(
            @Valid @RequestBody ShortenRequest request) {

        return ResponseEntity.ok(
                urlService.shortenUrl(request)
        );
    }


    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(
            @PathVariable String code) {

        UrlMapping mapping = urlRepository.findByShortCode(code)
                .orElse(null);

        if (mapping == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(mapping.getOriginalUrl()))
                .build();
    }
}
