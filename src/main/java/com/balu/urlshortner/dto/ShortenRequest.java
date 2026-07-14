package com.balu.urlshortner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ShortenRequest {

    @NotBlank(message = "URL is required")
    @Pattern(
        regexp = "^(http|https)://.*$",
        message = "Invalid URL format"
    )
    private String url;

    private String customAlias;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
}