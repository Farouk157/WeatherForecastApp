package com.example.weatherforecast
import com.example.weatherforecast.database.LocalDataSourceImpl
import com.example.weatherforecast.model.City
import com.example.weatherforecast.model.Coordinates
import com.example.weatherforecast.model.WeatherForecastResponse
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals


class LocalDataSourceImplTest {

    private lateinit var localDataSource: LocalDataSourceImpl
    private lateinit var mockWeatherDao: MockWeatherDao

    @Before
    fun setup() {
        mockWeatherDao = MockWeatherDao()
        localDataSource = LocalDataSourceImpl(mockWeatherDao)
    }

    @Test
    fun saveFavoritePlace_withWeatherData_insertsCorrectly() = runBlocking {
        // Arrange
        val city = City(
            name = "Test City",
            country = "Test Country",
            coord = Coordinates(0.0, 0.0),
            population = 100000,
            timezone = 0,
            sunrise = 0L,
            sunset = 0L
        )
        val weatherForecastResponse = WeatherForecastResponse(city, emptyList())

        // Act
        localDataSource.saveFavoritePlace(weatherForecastResponse)

        // Assert
        val favoritePlaces = mockWeatherDao.getFavoritePlaces().first()
        assertEquals(1, favoritePlaces.size)
        assertEquals("Test City", favoritePlaces[0].city.name) // Access the city name from the City object
    }

    @Test
    fun removeFavoritePlace_withCityName_deletesWeatherData() = runBlocking {
        // Arrange
        val city = City(
            name = "Test City",
            country = "Test Country",
            coord = Coordinates(0.0, 0.0),
            population = 100000,
            timezone = 0,
            sunrise = 0L,
            sunset = 0L
        )
        val weatherForecastResponse = WeatherForecastResponse(city, emptyList())
        mockWeatherDao.insertWeather(weatherForecastResponse) // Insert before removal

        // Act
        localDataSource.removeFavoritePlace(city.name)

        // Assert
        val favoritePlaces = mockWeatherDao.getFavoritePlaces().first()
        assertEquals(0, favoritePlaces.size) // Verify the list is empty after removal
    }
}
