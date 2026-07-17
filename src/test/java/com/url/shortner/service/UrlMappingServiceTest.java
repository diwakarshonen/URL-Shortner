package com.url.shortner.service;

import com.url.shortner.models.User;
import com.url.shortner.repository.ClickEventRepository;
import com.url.shortner.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UrlMappingServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @Mock
    private ClickEventRepository clickEventRepository;

    @InjectMocks
    private UrlMappingService urlMappingService;

    @Test
    void createShortUrl_shouldRejectInvalidUrl() {
        User user = new User();
        user.setId(1L);

        assertThrows(IllegalArgumentException.class, () -> urlMappingService.createShortUrl("not-a-valid-url", user));
    }
}
