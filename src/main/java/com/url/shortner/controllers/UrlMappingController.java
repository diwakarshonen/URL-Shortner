package com.url.shortner.controllers;

import com.url.shortner.dtos.ClickEventDTO;
import com.url.shortner.dtos.CreateUrlRequest;
import com.url.shortner.dtos.MessageResponse;
import com.url.shortner.dtos.UpdateUrlRequest;
import com.url.shortner.dtos.UrlMappingDTO;
import com.url.shortner.models.User;
import com.url.shortner.service.UrlMappingService;
import com.url.shortner.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
@Validated
public class UrlMappingController {

    private final UrlMappingService urlMappingService;
    private final UserService userService;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> createShortUrl(@Valid @RequestBody CreateUrlRequest request,
                                                        Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(urlMappingService.createShortUrl(request, user));
    }

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> getUserUrls(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(urlMappingService.getUrlsByUser(user));
    }

    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(@PathVariable @NotBlank String shortUrl,
                                                               @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        return ResponseEntity.ok(urlMappingService.getClickEventsByDate(shortUrl, start, end));
    }

    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(Principal principal,
                                                                     @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                                     @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(urlMappingService.getTotalClicksByUserAndDate(user, start, end));
    }

    @DeleteMapping("/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteShortUrl(@PathVariable @NotBlank String shortUrl,
                                                          Principal principal) {
        User user = userService.findByUsername(principal.getName());
        urlMappingService.deleteUrlMapping(shortUrl, user);
        return ResponseEntity.ok(new MessageResponse("Short URL deleted successfully", shortUrl));
    }

    @PutMapping("/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> updateOriginalUrl(@PathVariable @NotBlank String shortUrl,
                                                           @Valid @RequestBody UpdateUrlRequest request,
                                                           Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(urlMappingService.updateOriginalUrl(shortUrl,request, user));
    }
}
