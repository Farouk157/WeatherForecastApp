import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.database.AppDatabase
import com.example.weatherforecast.database.WeatherDao
import com.example.weatherforecast.model.City
import com.example.weatherforecast.model.Coordinates
import com.example.weatherforecast.model.WeatherForecastResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // For LiveData testing

    private lateinit var database: AppDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setUp() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        weatherDao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveFavoritePlaces() = runBlockingTest {
        // Arrange
        val city = City(
            name = "Test City",
            country = "Test Country",
            coord = Coordinates(0.0, 0.0),
            population = 0,
            timezone = 0,
            sunrise = 0,
            sunset = 0
        )
        val weatherData = WeatherForecastResponse(
            city = city,
            list = emptyList(), // You can provide a sample list if needed
            cityName = city.name
        )
        weatherDao.insertWeather(weatherData)

        // Act
        val result = weatherDao.getFavoritePlaces().first()

        // Assert
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("Test City", result[0].cityName)
    }

    @Test
    fun deleteFavoritePlaceByCityName() = runBlockingTest {
        // Arrange
        val city = City(
            name = "City to Delete",
            country = "Test Country",
            coord = Coordinates(0.0, 0.0),
            population = 0,
            timezone = 0,
            sunrise = 0,
            sunset = 0
        )
        val weatherData = WeatherForecastResponse(
            city = city,
            list = emptyList(), // You can provide a sample list if needed
            cityName = city.name
        )
        weatherDao.insertWeather(weatherData)

        // Act
        weatherDao.deleteFavoritePlace("City to Delete")
        val result = weatherDao.getFavoritePlaces().first()

        // Assert
        Assert.assertTrue(result.isEmpty())
    }
}