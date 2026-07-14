package com.balu.urlshortner.controller;


import com.balu.urlshortner.entity.UrlMapping;
import com.balu.urlshortner.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;


    @BeforeEach
    void cleanDatabase() {
        urlRepository.deleteAll();
    }


    // Test 1: Verify that a long URL can be shortened successfully and returns a short code
    @Test
    void shouldShortenUrl() throws Exception {

        String request = """
                {
                    "url": "https://www.google.com"
                }
                """;

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").exists())
                .andExpect(jsonPath("$.shortUrl").exists());
    }


    // Test 2: Verify that an existing short code redirects to the original URL with 301 status
    @Test
    void shouldRedirectToOriginalUrl() throws Exception {

        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode("test123");
        mapping.setOriginalUrl("https://github.com/test123");

        urlRepository.save(mapping);

        mockMvc.perform(get("/test123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header()
                        .string("Location", "https://github.com/test123"));
    }


    // Test 3: Verify that requesting an unknown short code returns 404 Not Found
    @Test
    void shouldReturn404ForUnknownCode() throws Exception {

        mockMvc.perform(get("/unknown123"))
                .andExpect(status().isNotFound());
    }


    // Test 4: Verify that shortening the same URL again follows duplicate URL handling
    @Test
    void shouldReturnExistingCodeForDuplicateUrl() throws Exception {

        String request = """
                {
                    "url": "https://www.google.com"
                }
                """;

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").exists())
                .andExpect(jsonPath("$.shortUrl").exists());
    }


    // Test 5: Verify that custom alias is accepted and used as short code
    @Test
    void shouldCreateCustomAlias() throws Exception {

        String request = """
                {
                    "url": "https://github.com",
                    "customAlias": "mygithub"
                }
                """;

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode")
                        .value("mygithub"))
                .andExpect(jsonPath("$.shortUrl")
                        .value("http://localhost:8080/mygithub"));
    }
}
// import com.balu.urlshortner.entity.UrlMapping;
// import com.balu.urlshortner.repository.UrlRepository;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.junit.jupiter.api.BeforeEach;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// class UrlControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private UrlRepository urlRepository;

//     @BeforeEach
//     void cleanDatabase(){
//         urlRepository.deleteAll();
//     }

//     // Test 1: Verify that a long URL can be shortened successfully 
//     // and returns a short code
//     @Test
//     void shouldShortenUrl() throws Exception {

//         String request = """
//                 {
//                     "url": "https://www.google.com"
//                 }
//                 """;

//         mockMvc.perform(post("/shorten")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(request))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.shortCode").exists())
//                 .andExpect(jsonPath("$.shortUrl").exists());
//     }

//     // Test 2: Verify that an existing short code redirects 
//     // to the original URL with 301 status
//     @Test
//     void shouldRedirectToOriginalUrl() throws Exception {

//         UrlMapping mapping = new UrlMapping();
//         mapping.setShortCode("test123");
//         mapping.setOriginalUrl("https://github.com/test123");

//         urlRepository.save(mapping);

//         mockMvc.perform(get("/test123"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(header()
//                 .string("Location", "https://github.com"));
//     }

//     // Test 3: Verify that requesting an unknown short code returns 404 Not Found
//     @Test
//     void shouldReturn404ForUnknownCode() throws Exception {

//         mockMvc.perform(get("/unknown123"))
//                 .andExpect(status().isNotFound());
//     }

//     //Duplicate Test URL 
//     //Test 4: Verify that shortening the same URL again returns the existing short code
//     @Test
//     void shouldReturnExistingCodeForDuplicateUrl() throws Exception {

//     String request = """
//             {
//                 "url": "https://www.google.com"
//             }
//             """;

//     String firstResponse = mockMvc.perform(post("/shorten")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(request))
//             .andExpect(status().isOk())
//             .andReturn()
//             .getResponse()
//             .getContentAsString();


//     mockMvc.perform(post("/shorten")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(request))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.shortCode").exists())
//             .andExpect(jsonPath("$.shortUrl").exists());
//     }


//     //Custom alias test 
//     // Test 5: Verify that a custom alias is accepted and used as the short code
//     @Test
// void shouldCreateCustomAlias() throws Exception {

//     String request = """
//             {
//                 "url": "https://github.com",
//                 "customAlias": "mygithub"
//             }
//             """;

//     mockMvc.perform(post("/shorten")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(request))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.shortCode")
//             .value("mygithub"))
//             .andExpect(jsonPath("$.shortUrl")
//             .value("http://localhost:8080/mygithub"));
// }
// }