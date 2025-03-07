# Weather Forecast Application

## Overview
The Weather Forecast Application is a Spring Boot-based REST API that provides weather forecast details based on a given ZIP code. It fetches real-time weather data from an external API, caches results for 30 minutes, and serves responses efficiently.

## Features
- Accepts ZIP code as input.
- Retrieves weather forecast details including current temperature, minimum, and maximum temperature.
- Caches weather data for 30 minutes to optimize performance.
- Provides an indicator when data is retrieved from the cache.
- Validates ZIP code format before processing.

## Technologies Used
- Java 17
- Spring Boot
- RestTemplate
- JUnit & Mockito (for testing)
- OpenWeather API / WeatherAPI.com (as weather data provider)
- ConcurrentHashMap (for caching)

## Setup and Installation
### Prerequisites
- Java 17 or later
- Maven

### Steps to Run
1. Clone the repository:
   ```sh
   git clone https://github.com/ketanbparekh/weatherapp.git
   cd weatherapp
   ```
2. Update the API key in `application.yml`:
   ```properties
   api.key:YOUR_API_KEY
   ```
3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```
4. Access the API:
   ```sh
   GET http://localhost:8080/api/v1/weather/forecast?zipCode=12345
   ```

## API Endpoints
### Get Weather Forecast
**Request:**
```
GET /api/v1/weather/forecast?zipCode={ZIP_CODE}
```

**Response:**
```json
{
  "currentTemperature": "48.90°F",
  "minTemperature": "27.10°F",
  "maxTemperature": "48.20°F",
  "cached": "false"
}
```

## Running Tests
Run unit tests with:
```sh
mvn test
```

## License
This project is licensed under the MIT License.

