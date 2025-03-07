package com.apple.weather;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.apple.model.WeatherForecast;
import com.apple.model.WeatherRequest;
import com.apple.model.WeatherResponse;
import com.apple.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Map<String, WeatherCache> mockMap;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "mock-api-key"); // Mock API key
        ReflectionTestUtils.setField(weatherService, "cache", new ConcurrentHashMap<>()); // Reset cache
    }

    @Test
    public void testValidZipCode_ReturnsWeatherData() {
        WeatherRequest request = new WeatherRequest();
        request.setZipCode("12345");

        WeatherResponse mockResponse = new WeatherResponse();
        WeatherResponse.Current current = new WeatherResponse.Current();
        current.temp_f = 55.0;
        mockResponse.setCurrent(current);

        WeatherResponse.Forecast day = new WeatherResponse.Forecast();
        WeatherResponse.ForecastDay forecastDay = new WeatherResponse.ForecastDay();
        forecastDay.day = new WeatherResponse.Day();
        forecastDay.day.maxtemp_f = 80.0;
        forecastDay.day.mintemp_f = 60.0;
        List<WeatherResponse.ForecastDay> forecastDayList = new ArrayList<>();
        forecastDayList.add(forecastDay);
        day.forecastday = forecastDayList;
        mockResponse.setForecast(day);

        when(restTemplate.getForEntity(anyString(), eq(WeatherResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        WeatherForecast response = weatherService.getWeatherForecastByZipCode(request);

        assertNotNull(response);
        assertEquals(55.00, response.getCurrentTemp());
        assertEquals(60.00, response.getMinTemp());
        assertEquals(80.00, response.getMaxTemp());
    }

    @Test
    public void testInvalidApiResponse_ThrowsException() {
        WeatherRequest request = new WeatherRequest();
        request.setZipCode("99999");

        when(restTemplate.getForEntity(anyString(), eq(WeatherResponse.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherForecastByZipCode(request);
        });

        assertTrue(thrown.getMessage().contains("Error: Could not retrieve weather data."));
    }

    @Test
    public void test_FetchCachedData() {
        WeatherRequest request = new WeatherRequest();
        request.setZipCode("67890");

        WeatherResponse mockResponse = new WeatherResponse();
        WeatherResponse.Current current = new WeatherResponse.Current();
        current.temp_f = 55.0;
        mockResponse.setCurrent(current);

        WeatherResponse.Forecast day = new WeatherResponse.Forecast();
        WeatherResponse.ForecastDay forecastDay = new WeatherResponse.ForecastDay();

        forecastDay.day = new WeatherResponse.Day();
        forecastDay.day.maxtemp_f = 80.0;
        forecastDay.day.mintemp_f = 60.0;

        List<WeatherResponse.ForecastDay> forecastDayList = new ArrayList<>();
        forecastDayList.add(forecastDay);
        day.forecastday = forecastDayList;
        mockResponse.setForecast(day);

        when(restTemplate.getForEntity(anyString(), eq(WeatherResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        //ForecastDetails forecastDetails =  new ForecastDetails(48.90, 27.10, 48.20, false );
        WeatherForecast response = weatherService.getWeatherForecastByZipCode(request);

        assertNotNull(response);
        assertEquals(55.00, response.getCurrentTemp());
        assertEquals(60.00, response.getMinTemp());
        assertEquals(80.00, response.getMaxTemp());

        Mockito.lenient().when(mockMap.get(anyString())).thenReturn(new WeatherCache("Cached Response: Current Temp: 55.00°F | Min: 60.00°F | Max: 80.00°F", 23434L));
        response = weatherService.getWeatherForecastByZipCode(request);

        assertNotNull(response);
        assertEquals(55.00, response.getCurrentTemp());
        assertEquals(60.00, response.getMinTemp());
        assertEquals(80.00, response.getMaxTemp());
    }

    private static class WeatherCache {
        private final String weatherData;
        private final long timestamp;
        private static final long EXPIRATION_TIME = 60 * 30 * 1000;

        private WeatherCache(String weatherData, long timestamp) {
            this.weatherData = weatherData;
            this.timestamp = timestamp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > EXPIRATION_TIME;
        }
    }
}
