package com.example.myweather.model

import com.example.myweather.database.FakeLocalSource
import com.example.myweather.database.LocalSource
import com.example.myweather.network.FakeRemoteSource
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepositoryTest {
    private var favList: MutableList<Favourite> = mutableListOf(
        Favourite("Egypt",2.6, 5.3),
        Favourite("Paris", 8.3, 9.7),
        Favourite("Greece", 7.4, 6.6)
    )
    var responseExample = ResponseModel(
        lat = 2.5,
        lon = 5.8,
        current = null,
        daily = listOf(),
        hourly = listOf(),
        timezone = "Luxor",
        timezoneOffset = 0L
    )

    lateinit var remote: FakeRemoteSource
    lateinit var local: FakeLocalSource
    lateinit var repo: Repository

    @Before
    fun setUp(){
        remote= FakeRemoteSource((responseExample))
        local= FakeLocalSource(favList)
        repo= Repository.getInstance(remote,local)
    }

    @Test
    fun getCurrentWeather_ResponseOfWeather() = runBlockingTest {
        // when - > call getCurrentWeather method
        val response= repo.getCurrentWeather(2.2,3.3,"en","standard","8beb73e4a526e79ac6ebf8f114f7ee43")
        var responseModel: ResponseModel? = null
        response.collect{
            responseModel= it
        }

        //Then -> object of ResponseModel is equivalent to the passed object to RemoteSource
        assertThat(responseModel?.timezone , `is`("Luxor"))
    }
}