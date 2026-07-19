package com.url.shortner.service;

import com.url.shortner.dtos.ClickEventDTO;
import com.url.shortner.dtos.CreateUrlRequest;
import com.url.shortner.dtos.UpdateUrlRequest;
import com.url.shortner.dtos.UrlMappingDTO;
import com.url.shortner.exceptions.ForbiddenException;
import com.url.shortner.exceptions.ResourceNotFoundException;
import com.url.shortner.models.ClickEvent;
import com.url.shortner.models.UrlMapping;
import com.url.shortner.models.User;
import com.url.shortner.repository.ClickEventRepository;
import com.url.shortner.repository.UrlMappingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_URL_LENGTH = 8;

    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventRepository clickEventRepository;

    public UrlMappingDTO createShortUrl(CreateUrlRequest request, User user) {
        Objects.requireNonNull(user, "User must not be null");
        validateOriginalUrl(request.getOriginalUrl());
        String shortUrl = generateShortUrl();
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(request.getOriginalUrl().trim());
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        urlMapping.setDescription(request.getDescription());
        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);
        return convertToDto(savedUrlMapping);
    }

    private void validateOriginalUrl(String originalUrl) {
        if (!StringUtils.hasText(originalUrl)) {
            throw new IllegalArgumentException("Original URL cannot be blank");
        }

        try {
            URI uri = new URI(originalUrl.trim());
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new IllegalArgumentException("Original URL must use http or https");
            }
            if (!StringUtils.hasText(uri.getHost())) {
                throw new IllegalArgumentException("Original URL must include a valid host");
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Original URL must be a valid http/https URL", ex);
        }
    }

    private String generateShortUrl() {
        SecureRandom random = new SecureRandom();
        String shortUrl;

        do {
            StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);
            for (int i = 0; i < SHORT_URL_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            shortUrl = sb.toString();
        } while (urlMappingRepository.existsByShortUrl(shortUrl));

        return shortUrl;
    }

    private UrlMappingDTO convertToDto(UrlMapping urlMapping) {
        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setId(urlMapping.getId());
        urlMappingDTO.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDTO.setShortUrl(urlMapping.getShortUrl());
        urlMappingDTO.setClickCount(urlMapping.getClickCount());
        urlMappingDTO.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingDTO.setUsername(urlMapping.getUser().getUsername());
        urlMappingDTO.setDescription(urlMapping.getDescription());
        return urlMappingDTO;
    }

    public List<UrlMappingDTO> getUrlsByUser(User user) {
        return urlMappingRepository.findByUser(user).stream().map(this::convertToDto).toList();
    }

    public List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping == null) {
            return Collections.emptyList();
        }

        return clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, start, end)
                .stream()
                .collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    ClickEventDTO clickEventDTO = new ClickEventDTO();
                    clickEventDTO.setClickDate(entry.getKey());
                    clickEventDTO.setCount(entry.getValue());
                    return clickEventDTO;
                })
                .toList();
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        if (urlMappings.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, start.atStartOfDay(), end.plusDays(1).atStartOfDay());
        return clickEvents.stream().collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()));
    }

    @Transactional
    public UrlMapping getOriginalUrl(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping != null) {
            urlMapping.setClickCount(urlMapping.getClickCount() + 1);
            urlMappingRepository.save(urlMapping);

            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setClickDate(LocalDateTime.now());
            clickEvent.setUrlMapping(urlMapping);
            clickEventRepository.save(clickEvent);
        }
        return urlMapping;
    }

    @Transactional
    public void deleteUrlMapping(String shortUrl, User user) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping == null) {
            throw new ResourceNotFoundException("URL mapping not found");
        }
        if (urlMapping.getUser() == null || !Objects.equals(urlMapping.getUser().getId(), user.getId())) {
            throw new ForbiddenException("You don't have permission to delete this URL");
        }

        clickEventRepository.deleteByUrlMapping(urlMapping);
        urlMappingRepository.delete(urlMapping);
    }

    @Transactional
    public UrlMappingDTO updateOriginalUrl(String shortUrl, UpdateUrlRequest request, User user) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping == null) {
            throw new ResourceNotFoundException("URL mapping not found");
        }
        if (urlMapping.getUser() == null || !Objects.equals(urlMapping.getUser().getId(), user.getId())) {
            throw new ForbiddenException("You don't have permission to update this URL");
        }

        validateOriginalUrl(request.getOriginalUrl());
        urlMapping.setOriginalUrl(request.getOriginalUrl().trim());
        urlMapping.setDescription(request.getDescription());
        urlMappingRepository.save(urlMapping);
        return convertToDto(urlMapping);
    }
}
