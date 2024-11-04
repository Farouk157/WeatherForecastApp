package com.example.weatherforecast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherforecast.favoritefragment.viewmodel.FavoriteFragmentViewModel
import com.example.weatherforecast.model.City
import com.example.weatherforecast.model.Coordinates
import com.example.weatherforecast.model.WeatherForecastResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteFragmentViewModelTest {

    private lateinit var viewModel: FavoriteFragmentViewModel
    private lateinit var mockRepository: MockWeatherRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val mockLocalDataSource = MockLocalDataSource()
        val mockRemoteDataSource = MockWeatherRemoteDataSource()
        mockRepository = MockWeatherRepository(mockLocalDataSource, mockRemoteDataSource)
        viewModel = FavoriteFragmentViewModel(mockRepository)
    }

    @Test
    fun `insertFavourite adds weather forecast response to repository`() = runTest {
        // Given
        val weatherForecastResponse = WeatherForecastResponse(
            city = City("New City", "New Country", Coordinates(2.0, 2.0), 3000, 0, 0L, 0L),
            list = emptyList()
        )

        // When
        viewModel.saveFavoritePlace(weatherForecastResponse)
        val favoritePlaces = viewModel.favoritePlaces.first()

        // Then
        assertThat(favoritePlaces.contains(weatherForecastResponse), `is`(true))
    }

    @Test
    fun `removeFavourite removes weather forecast response from repository`() = runTest {
        // Given
        val weatherForecastResponse = WeatherForecastResponse(
            city = City("City1", "Country1", Coordinates(0.0, 0.0), 1000, 0, 0L, 0L),
            list = emptyList()
        )
        viewModel.saveFavoritePlace(weatherForecastResponse)

        // When
        viewModel.removeFavoritePlace("City1")
        val favoritePlaces = viewModel.favoritePlaces.first()

        // Then
        assertThat(favoritePlaces.contains(weatherForecastResponse), `is`(false))
    }

}
