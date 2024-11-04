package com.example.weatherforecast

import com.example.weatherforecast.model.City
import com.example.weatherforecast.model.Coordinates
import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.model.WeatherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var mockWeatherRemoteDataSource: MockWeatherRemoteDataSource
    private lateinit var mockLocalDataSource: MockLocalDataSource

    @Before
    fun setup() {
        mockWeatherRemoteDataSource = MockWeatherRemoteDataSource()
        mockLocalDataSource = MockLocalDataSource()
        weatherRepository = WeatherRepository(mockWeatherRemoteDataSource, mockLocalDataSource)
    }

    @Test
    fun fetchWeatherByLocation_withCityName_returnsWeatherDataFromRemoteSource() = runBlocking {
        // Arrange
        val expectedResponse = WeatherForecastResponse(
            city = City("Sample City", "Sample Country", Coordinates(1.0, 1.0), 100000, 0, 0L, 0L),
            list = emptyList()
        )
        mockWeatherRemoteDataSource.mockResponse = expectedResponse

        // Act
        val actualResponse = weatherRepository.fetchWeatherByLocation("Sample City", "metric", "en")

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun saveFavoritePlace_withWeatherData_addsToLocalDataSource() = runBlocking {
        // Arrange
        val weatherForecastResponse = WeatherForecastResponse(
            city = City("Test City", "Test Country", Coordinates(1.0, 1.0), 100000, 0, 0L, 0L),
            list = emptyList()
        )

        // Act
        weatherRepository.saveFavoritePlace(weatherForecastResponse)

        // Assert
        val favoritePlaces = mockLocalDataSource.getFavoritePlaces().first()
        assertEquals(1, favoritePlaces.size)
        assertEquals("Test City", favoritePlaces[0].city.name)
    }
}