package com.balu.urlshortner.service;

import com.balu.urlshortner.dto.ShortenRequest;
import com.balu.urlshortner.dto.ShortenResponse;
import com.balu.urlshortner.entity.UrlMapping;
import com.balu.urlshortner.repository.UrlRepository;
import com.balu.urlshortner.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public ShortenResponse shortenUrl(ShortenRequest request) {

        // Check if URL already exists
        var existingUrl = urlRepository.findByOriginalUrl(request.getUrl());

        if (existingUrl.isPresent()) {
            UrlMapping mapping = existingUrl.get();

            return new ShortenResponse(
                    mapping.getShortCode(),
                    "http://localhost:8080/" + mapping.getShortCode()
            );
        }

        String code;

        // Custom alias handling
        if (request.getCustomAlias() != null &&
                !request.getCustomAlias().isBlank()) {

            if (urlRepository.existsByShortCode(request.getCustomAlias())) {
                throw new RuntimeException("Alias already exists");
            }

            code = request.getCustomAlias();

        } else {
            do {
                code = ShortCodeGenerator.generateCode();
            } while (urlRepository.existsByShortCode(code));
        }


        UrlMapping mapping = new UrlMapping();

        mapping.setOriginalUrl(request.getUrl());
        mapping.setShortCode(code);
        mapping.setCustomAlias(request.getCustomAlias());

        urlRepository.save(mapping);


        return new ShortenResponse(
                code,
                "http://localhost:8080/" + code
        );
    }
}