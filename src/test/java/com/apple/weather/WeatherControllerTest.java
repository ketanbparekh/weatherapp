package com.apple.weather;

import com.apple.WeatherApplication;
import com.apple.controller.WeatherController;
import com.apple.model.WeatherForecast;
import com.apple.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = WeatherApplication.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @Autowired
    private WebApplicationContext context;

    @Test
    public void testValidZipCode_ReturnsForecast() throws Exception {

        String mockResponse = "{\"currentTemp\":48.9,\"minTemp\":27.1,\"maxTemp\":48.2,\"cached\":false}";
        WeatherForecast weatherForecast = new WeatherForecast(48.90, 27.10,48.20, false);
        when(weatherService.getWeatherForecastByZipCode(any())).thenReturn(weatherForecast);

        mockMvc.perform(get("/api/v1/weather/forecast")
                        .param("zipCode", "12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    public void testInvalidZipCode_ReturnsBadRequest() throws Exception {

        mockMvc.perform(get("/api/v1/weather/forecast")
                        .param("zipCode", "1234")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testZipCodeWithHyphen_ReturnsForecast() throws Exception {
        String mockResponse = "{\"currentTemp\":48.9,\"minTemp\":27.1,\"maxTemp\":48.2,\"cached\":false}";
        WeatherForecast weatherForecast = new WeatherForecast(48.90, 27.10,48.20, false);
        when(weatherService.getWeatherForecastByZipCode(any())).thenReturn(weatherForecast);

        mockMvc.perform(get("/api/v1/weather/forecast")
                        .param("zipCode", "12345-6789")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}
