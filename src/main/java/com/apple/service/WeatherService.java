package com.apple.service;

import com.apple.model.WeatherForecast;
import com.apple.model.WeatherRequest;
import com.apple.model.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WeatherService {

    private Map<String, WeatherCache> cache = new ConcurrentHashMap<String, WeatherCache>();

    private final String API_URL = "http://api.weatherapi.com/v1/forecast.json?key=%s&q=%s&days=1&aqi=no&alerts=no";

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public WeatherForecast getWeatherForecastByZipCode(WeatherRequest weatherRequest) {
        if (cache.containsKey(weatherRequest.getZipCode()) ) {
            WeatherCache weatherCache = cache.get(weatherRequest.getZipCode());
            if(!weatherCache.isExpired()){
                log.info("Returning cached response");
                WeatherForecast forecastDetail = cache.get(weatherRequest.getZipCode()).forecastDetail;
                forecastDetail.setCached(true);
                return forecastDetail;
            }
            //remove expired cache entry
            cache.remove(weatherRequest);
        }

        String urlString = String.format(API_URL, apiKey, weatherRequest);
        ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(urlString, WeatherResponse.class);
        int responseCode = response.getStatusCode().value();
        if (responseCode != 200) {
            log.error("Error: Could not retrieve weather data.")            ;
            throw new RuntimeException("Error: Could not retrieve weather data.");
        }

        WeatherResponse weatherResponse = response.getBody();

        //retreiving temperatures in Farenheight as default, api also provides temperature in Celcius.
        double temp = weatherResponse.getCurrent().temp_f;
        double maxForDay = weatherResponse.getForecast().forecastday.get(0).day.maxtemp_f;
        double minForDay = weatherResponse.getForecast().forecastday.get(0).day.mintemp_f;

        WeatherForecast weatherForecast = new WeatherForecast(temp, minForDay, maxForDay, false);
        cache.put(weatherRequest.getZipCode(), new WeatherCache(weatherForecast, System.currentTimeMillis()));
        return weatherForecast;
    }

    private static class WeatherCache {
        private final WeatherForecast forecastDetail;
        private final long timestamp;
        private static final long EXPIRATION_TIME = 60 * 30 * 1000;

        private WeatherCache(WeatherForecast forecastDetail, long timestamp) {
            this.forecastDetail = forecastDetail;
            this.timestamp = timestamp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > EXPIRATION_TIME;
        }
    }
}
