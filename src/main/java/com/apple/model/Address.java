package com.apple.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class Address {

    private String address;
    private String city;
    private String state;

    @NotBlank
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Invalid zip code format")
    private String zipCode;
}
