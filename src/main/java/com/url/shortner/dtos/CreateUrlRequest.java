package com.url.shortner.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUrlRequest {

    @NotBlank(message = "Original URL cannot be blank")
    private String originalUrl;

    private String description;
}
