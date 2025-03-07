package com.apple.model;

import com.apple.validator.ValidZipCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class WeatherRequest {
    private String address;
    private String city;
    private String state;

    @NotBlank(message = "ZIP code is required")
    @ValidZipCode
    private String zipCode;
}
