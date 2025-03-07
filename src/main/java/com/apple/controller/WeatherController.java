package com.apple.controller;

import com.apple.model.WeatherForecast;
import com.apple.model.WeatherRequest;
import com.apple.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/weather")
@Slf4j
public class WeatherController {

    private static final String ZIP_CODE_REGEX = "^\\d{5}(?:-\\d{4})?$";

    private static final Pattern zipCodePattern = Pattern.compile(ZIP_CODE_REGEX);

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/forecast")
    public WeatherForecast getForecastByZipCode(@RequestParam String zipCode){

        if(!validateZipCode(zipCode)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ZipCode value cannot be empty or incorrect format");
        }
        WeatherRequest weatherRequest = new WeatherRequest();
        weatherRequest.setZipCode(zipCode);
        return weatherService.getWeatherForecastByZipCode(weatherRequest);
    }

    private boolean validateZipCode(String zipCode){
        return zipCode != null && zipCodePattern.matcher(zipCode).matches();
    }
}
