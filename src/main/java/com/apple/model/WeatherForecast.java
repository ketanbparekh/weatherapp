package com.apple.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherForecast {
    private double currentTemp;
    private double minTemp;
    private double maxTemp;
    private boolean cached;
}
