package com.url.shortner.service;

import com.url.shortner.models.UrlMapping;
import com.url.shortner.repository.ClickEventRepository;
import com.url.shortner.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UrlMappingServiceCachingTest.TestConfig.class)
class UrlMappingServiceCachingTest {

    @Configuration
    @EnableCaching
    static class TestConfig {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("urlMappings", "userUrls");
        }

        @Bean
        UrlMappingRepository urlMappingRepository() {
            return Mockito.mock(UrlMappingRepository.class);
        }

        @Bean
        ClickEventRepository clickEventRepository() {
            return Mockito.mock(ClickEventRepository.class);
        }

        @Bean
        UrlMappingService urlMappingService(UrlMappingRepository urlMappingRepository,
                                            ClickEventRepository clickEventRepository) {
            return new UrlMappingService(urlMappingRepository, clickEventRepository);
        }
    }

    @Autowired
    private UrlMappingService urlMappingService;

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Test
    void findByShortUrlCached_shouldUseCacheForRepeatedLookups() {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortUrl("abc12345");
        mapping.setOriginalUrl("https://example.com");

        when(urlMappingRepository.findByShortUrl("abc12345")).thenReturn(mapping);

        UrlMapping first = urlMappingService.findByShortUrlCached("abc12345");
        UrlMapping second = urlMappingService.findByShortUrlCached("abc12345");

        assertNotNull(first);
        assertSame(first, second);
        verify(urlMappingRepository, times(1)).findByShortUrl("abc12345");
    }
}
